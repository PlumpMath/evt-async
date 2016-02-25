(ns evt.api
  (:use [evt.net]
        [clojure.core.async :only [go-loop >! <! close!]]))

(defn next-url [response]
  "Return the URL of the next page of the result set,
   or nil."
  (get-in response [:links :next :href]))

(defn paginate [key first-page-url ch]
  "Resource at given url is expected to return a list
   of results, and a link to the next page of results.
   Each record is written to the given channel."
  (go-loop [url first-page-url]
    (let [response (get-json key url)
          data (body response)]
      (doseq [row data]
        (>! ch row))
      (if-let [next-page (next-url response)]
        (recur next-page)
        (close! ch)))))

(defn echo [ch]
  (go-loop []
    (if-let [id (<! ch)]
      (do
        (println (str id))
        (recur))
      (println "Finished."))))