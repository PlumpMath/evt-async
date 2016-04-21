(ns evt.api
  (:require [evt.res :as r])
  (:use [evt.net]
        [clojure.core.async :only [go-loop >! <! close! chan timeout]])
  (:import (java.net URL)))

(def EVRYTHNG_API_KEY "EVRYTHNG_API_KEY")
(def EVRYTHNG_API_URL "EVRYTHNG_API_URL")

(def EVT-API "https://api.evrythng.com")

(defn with-account
  "Returns an function 'evt' that interacts with the EVT account specified
   by the given key (and optionally a different API URL). The evt function
   takes an action function (paginate or drain), a resource, and an optional filter.
   EVT returns a channel containing the results of running the action."
  ([key] (with-account key EVT-API))
  ([key base-url]
    (let [api-base-url (URL. base-url)]
      (fn [action resource]
        (let [url (r/resource-url api-base-url resource)]
          (action key url))))))


(defn with-default-account []
  "Return a function that connects with your default EVT account.
   Your system MUST have an environment variable EVRYTHNG_API_KEY,
   and MAY have an have an environment variable EVRYTHNG_API_URL which
   overrides the default API location."
  (with-account
    (System/getenv EVRYTHNG_API_KEY)
    (or (System/getenv EVRYTHNG_API_URL) EVT-API)))


(defn next-url [response]
  "Return the URL of the next page of the result set,
   or nil."
  (get-in response [:links :next :href]))

(defn paginate
  "Resource at given url is expected to return a list
   of results, and a link to the next page of results.
   Each record is written to the given channel."
  ([key first-page-url] (paginate key first-page-url (chan 1000)))
  ([key first-page-url ch]
   (do
     (go-loop [url first-page-url]
      (let [response (get-json key url)
            data (body response)
            result-count (get-in response [:headers "x-result-count"] "0")]
        (println (format "%d, %s records remain. %s" (:status response) result-count (:headers response)))
        (doseq [row data]
          (>! ch row))
        (if-let [next-page (next-url response)]
          (recur next-page)
          (close! ch))))
     ch)))

(defn drain
  "Keep retrieving same URL, until zero results found"
  ([key url] (drain key url (chan 10000)))
  ([key url ch]
   (do
     (go-loop [page 1]
       (let [response (get-json key (str url "&page=" page))
             data (body response)
             result-count     (get-in response [:headers "x-result-count"] "0")
             rate-limit-reset (get-in response [:headers "X-Rate-Limit-Reset"] "")]
         (println (format "%d, %s records remain. X-Rate-Limit-Reset: %s" (:status response) result-count rate-limit-reset))
         (doseq [row data]
           (>! ch row))
         (if (or
               (zero? (Integer/parseInt result-count))
               (empty? data))
           (close! ch)
           (recur (inc page)))))
     ch)))

(defn echo [ch]
  (go-loop []
    (if-let [row (<! ch)]
      (do
        (println (str [(:id row) (:name row) (:description row) (:tags row)]))
        (recur))
      (println "Finished."))))

(defn delete-all [ch res-url]
  (go-loop []
    (if-let [row (<! ch)]
      (let [id (:id row)
            url (res-url id)]
        (let [result (evt.net/delete evt.api/EVRYTHNG_API_KEY url)]
          (println (format "Deletion: %s Status: %d" id (:status result))))
        (<! (timeout 999))
        (recur))
      (println "Finished."))))


(defn delete-all-actions [ch res-url]
  (go-loop []
    (if-let [row (<! ch)]
      (let [id (:id row)
            at  (:type row)
            url (res-url at id)]
        (let [result (evt.net/delete evt.api/EVRYTHNG_API_KEY url)]
          (println (format "Deletion: %s %s Status: %d" at id (:status result))))
        (<! (timeout 250))
        (recur))
      (println "Finished."))))

