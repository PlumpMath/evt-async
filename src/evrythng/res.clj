(ns evrythng.res
  (:import [java.net URL])
  (:require [evt.filters :as f]))

; Resource URLs for evrythng.com

(def resource-paths {:thngs "thngs"
                     :products "products"})

(defn resource-url [^URL base-url resource]
  (->>
    (get resource-paths resource (str resource) )
    (URL. base-url)
    (str)))


(def EVT-API (URL. "https://api.evrythng.com"))

(def collections-url (str (URL. EVT-API "collections")))
(def products-url    (str (URL. EVT-API "products")))
(def thngs-url       (str (URL. EVT-API "thngs")))
(def batches-url     (str (URL. EVT-API "batches")))
(def actions-url     (str (URL. EVT-API "actions")))

(defn products-tagged [tag]
  (str products-url "?filter=tags=" tag "&perPage=" 100))

(defn product [id]
  (str products-url "/" id))

; Thngs

(defn thng [id]
  (str thngs-url "/" id))

(defn thngs-tagged [tag]
  (str thngs-url "?filter=tags=" tag "&perPage=" 100))

; Collections

(defn collection [id]
  (str collections-url "/" id))

(defn collections-tagged [tag]
  (str collections-url "?filter=tags=" tag "&perPage=" 100))

; Batches

(defn batches-tagged [tag]
  (str batches-url "?filter=tags=" tag))

(defn batch [id]
  (str batches-url "/" id))

; Actions

(defn actions-of [at]
	(str actions-url "/" at))

(defn actions-of-tagged [at tag]
  (str actions-url "/" at "?perPage=100" "&filter=tags=" tag))


(defn action [at id]
	(clojure.string/join "/" [actions-url at id]))

(defn products-filter [id f]
	"All products that are returned by filter"
	(str products-url "?" (f/params f)))
