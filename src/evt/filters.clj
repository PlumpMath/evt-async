(ns evt.filters)

;
; Functions for building Filters for the EVT API
;
; https://dashboard.evrythng.com/documentation/api/filters
;

(def default-per-page 30)
(def max-per-page 100)

(def default-filter {
                     :perPage default-per-page
                     :tags []})

(defn page
  "Modify filter to return page n"
  ([n]
   (page default-filter n))
  ([f n] {:pre [(pos? n)]}
   (assoc f :page n)))

(defn tagged
  "Modify filter to return object with tag"
  ([tag] (tagged default-filter tag))
  ([f tag] (let [tags (get-in f [:tags] [])]
    (assoc f :tags (conj tags tag)))))
