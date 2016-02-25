(ns evt.net
  (:require [clj-http.client :as h])
  (:use [cheshire.core :only [generate-string]]))

;#? (:clj

(defn auth-header [req key]
  "Header to send API key"
  (assoc-in req [:headers "Authorization" ] key ))

(defn json-header [req]
  "Headers to request JSON"
  (merge req {:accept :json
              :as :json}))

(defn evt-headers [key]
  (->
    (json-header {})
    (auth-header key)))

(defn get-json [key url]
  "GET the given url as JSON, returns a map."
    (h/get url (evt-headers key)))

(defn delete [key url]
  "Send HTTP DELETE to given URL with given key"
  (h/delete url (evt-headers key)))

(defn body [res]
  "The body of an HTTP response as a map"
  (:body res))
