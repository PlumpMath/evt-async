(ns evt.print)

(defn echo [thng]
  (println (:id thng)))

(defn echo-id [thng]
  "Echo the ID of the thng to STDOUT"
  (println (:id thng)))
