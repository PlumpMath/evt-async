(ns evt.res
  (:import [java.net URL])
  (:require [evt.filters :as f]))

; Resource URLs for evrythng.com

(def EVT-API (URL. "https://api.evrythng.com"))

(def collections-url (str (URL. EVT-API "collections")))
(def products-url    (str (URL. EVT-API "products")))
(def thngs-url       (str (URL. EVT-API "thngs")))
(def batches-url     (str (URL. EVT-API "batches")))

(defn products-tagged [tag]
  (str products-url "?filter=tags=" tag "&perPage=" 10))

(defn thngs-tagged [tag]
  (str thngs-url "?filter=tags=" tag))

(defn product [id]
  (str products-url "/" id))

(defn products-filter [id f]
	"All products that are returned by filter"
	(str products-url "?" (f/params f)))
