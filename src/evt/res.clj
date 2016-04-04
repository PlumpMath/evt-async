(ns evt.res
  (:import [java.net URL]))

; Resource URLs for evrythng.com

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

(defn action [at id]
	(clojure.string/join "/" [actions-url at id]))