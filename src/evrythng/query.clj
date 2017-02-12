(ns evrythng.query)

; Writes queries against EVT things using transducers


;(defn tagged [tag]
 ; (filter #()))

(defn description [thng]
  (:description thng))

(defn description-contains [^String s]
  "Return a filter that tests to see if description contains String s"
  (let [s (.toLowerCase s)]
        (filter #(.contains (.toLowerCase (description %)) s))))
