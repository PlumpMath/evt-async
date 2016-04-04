(ns evt.filters)

;
; Functions for building Filters for the EVT API
;
; https://dashboard.evrythng.com/documentation/api/filters
;

(def default-per-page  30)
(def max-per-page     100)

(def default-filter {
                     :perPage default-per-page
                     :tags []})

(defn page
  "Modify filter to return page n"
  ([n]
   (page default-filter n))
  ([f n] {:pre [(pos? n)]}
   (assoc f :page n)))

(defn next-page [f]
  "Modify filter to return next page"
  (let [p (get f :page 1)]
    (assoc f :page (inc p))))

(defn tagged
  "Modify filter to return object with tag"
  ([tag] (tagged default-filter tag))
  ([f tag] (let [tags (get-in f [:tags] [])]
    (assoc f :tags (conj tags tag)))))

(defn in-project
  "Modify filter to return only thngs in a Project"
  ([project-id] (in-project default-filter project-id))
  ([f project-id] (assoc f :project project-id)))

(defn params [f]
  (let [tags (map #(format "filter=tags=%s" %) (:tags f))
        scope [(when (contains? f :page)    (format "page=%d"    (:page f)))
               (when (contains? f :perPage) (format "perPage=%d" (:perPage f)))
               (when (contains? f :project) (format "project=%s" (:project f)))]]
  (->>
    (into scope tags)
    (remove nil?)
    (clojure.string/join "&"))))

(defn url-params [url f]
  (str url "?" (params f)))
