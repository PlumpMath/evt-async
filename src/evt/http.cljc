(ns evt.http
  ;#? (:clj
  (:require
            [clojure.tools.logging :as log]

            )
  (:use [cheshire.core :only [generate-string]]
        [evt.res]))






(defn delete-product [id]
  (h/delete (str products-url "/" id)
            {:headers {"Authorization" EVRYTHNG_API_KEY}
             :accept :json
             :as :json}))


(defn delete-collection [id]
  (h/delete (str collections-url "/" id)
            {:headers {"Authorization" EVRYTHNG_API_KEY}
             :accept :json
             :as :json}))

(defn delete-batch [id]
  (h/delete (str batches-url "/" id)
            {:headers {"Authorization" EVRYTHNG_API_KEY}
             :accept :json
             :as :json}))


(defn post-json [url body]
  (h/post url
          {:headers {"Authorization" EVRYTHNG_API_KEY}
           :accept :json
           :content-type :json
           :as :json
           :body (generate-string body)}))



(defn ids [doc]
  "The IDs of the objects"
  (map :id doc))

(def body-ids
  "The IDs of the objects in the body of the response"
  (comp ids body))

(defn same-host? [url1 url2]
  (= (.getHost url1) (.getHost url2)))

(defn diff-path? [url1 url2]
  (not= (.getPath url1) (.getPath url2)))





; (paginate products-url (comp println body-ids))
