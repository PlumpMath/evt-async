(ns evt.print)

(defn echo [thng]
  "Echo the id and name of the thng"
  (println
    (format "%s %s"
            (:id thng)
            (:name thng))))

(defn echo-id [thng]
  "Echo the ID of the thng to STDOUT"
  (println (:id thng)))

(defn echo-tags [thng]
  "Echo the id, name and tags of the thng"
  (println
    (format "%s %s %s"
            (:id thng)
            (get thng :name "Unnamed")
            (str (get thng :tags [])))))
