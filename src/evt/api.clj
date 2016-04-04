(ns evt.api
  (:require [evt.filters :as fltr])
  (:use [evt.net]
        [clojure.core.async :only [go-loop >! <! close! chan timeout]]))

(def EVRYTHNG_API_KEY (System/getenv "EVRYTHNG_API_KEY"))

(defn next-url [response]
  "Return the URL of the next page of the result set,
   or nil."
  (get-in response [:links :next :href]))

(defn paginate
  "Resource at given url is expected to return a list
   of results, and a link to the next page of results.
   Each record is written to the given channel."
  ([key first-page-url] (paginate key first-page-url (chan 100)))
  ([key first-page-url ch]
   (do
     (go-loop [url first-page-url]
      (let [response (get-json key url)
            data (body response)]
        (doseq [row data]
          (>! ch row))
        (if-let [next-page (next-url response)]
          (recur next-page)
          (close! ch))))
     ch)))

(defn drain
  "Keep retrieving same URL, until zero results found"
  ([key url] (drain key url (chan 100)))
  ([key url ch]
   (do
     (go-loop [page 1]
       (let [response (get-json key (str url "&page=" page))
             data (body response)
             n (get-in response [:headers "x-result-count"] "0")]
         (doseq [row data]
           (>! ch row))
         (println (str "X-Rate-Limit-Reset: " (get-in response [:headers "X-Rate-Limit-Reset"])))
         (if (zero? (Integer/parseInt n))
           (close! ch)
           (recur (inc page)))))
     ch)))


(defn query [key url f1 q]
  "Paginate over all records returned by URL and filter f, 
   applying transducer q before thngs are added
   to the channel"
   (let [ch (chan 300 q)]
    (go-loop [f f1]
      (let [url (fltr/url-params url f)
            response (get-json key url)
            body     (body response)
            n (get-in response [:headers "x-result-count"] "0")]
        (println (format "%s returned %s records" url n))
        (doseq [thng body]
           (>! ch thng))
        (if (zero? (count body))
           (close! ch)
           (recur (fltr/next-page f)))))
    ch))


(defn echo [ch]
  "Echo description of every thng in the channel"
  (go-loop []
    (if-let [row (<! ch)]
      (do
        (println (str [(:id row) 
                       (:name row) 
                       (:description row) 
                       (:tags row)]))
        (recur))
      (println "Finished."))))

(defn delete-all [ch res-url]
  (go-loop []
    (if-let [row (<! ch)]
      (let [id (:id row)
            url (res-url id)]
        (println (str "Deleting: " url))
        (evt.net/delete evt.api/EVRYTHNG_API_KEY url)
        (<! (timeout 125))
        (recur))
      (println "Finished."))))
