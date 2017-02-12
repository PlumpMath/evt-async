(ns evrythng.api
  (:require [evt.res :as r]
            [evt.filters :as fltr])
  (:use [evt.net]
        [clojure.core.async :only [go-loop >! <! close! chan timeout]])
  (:import (java.net URL)))

; Accounts

(def EVRYTHNG_API_KEY "EVRYTHNG_API_KEY")
(def EVRYTHNG_API_URL "EVRYTHNG_API_URL")

(def EVT-API "https://api.evrythng.com")

(defn with-account
  "Returns an function 'evrythng' that interacts with the EVT account specified
   by the given key (and optionally a different API URL). The evrythng function
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


; Channels

(defn eager-chan
  "Make a channel with the capacity to read ahead.
   The EVT API allows 100 records to be read at a time"
  ([]       (chan (* 10 100)))
  ([tducer] (chan (* 10 100 tducer))))

; Pagination

(defn next-url [response]
  "Return the URL of the next page of the result set,
   or nil."
  (get-in response [:links :next :href]))

(defn paginate
  "Resource at given url is expected to return a list
   of results, and a link to the next page of results.
   Each record is written to the given channel."
  ([key first-page-url] (paginate key first-page-url (eager-chan)))
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

; Cursor

(defn drain
  "Keep retrieving same URL, until zero results found"
  ([key url] (drain key url (eager-chan)))
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


(defn query [key url f1 q]
  "Paginate over all records returned by URL and filter f, 
   applying transducer q before thngs are added
   to the channel"
   (let [ch (eager-chan q)]
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


; Workers

(defn for-each [ch f]
  "Apply function to every thng in the channel.
   Finishes when the channel is closed.
   TODO stop if we get HTTP 503"
  (go-loop []
    (if-let [row (<! ch)]
      (do
        (f row)
        (recur))
      (println "Finished."))))
