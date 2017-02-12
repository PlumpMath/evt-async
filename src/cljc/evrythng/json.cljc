(ns evrythng.json "JSON functions"
  (:require #?(:clj [clojure.core.async :as a :refer [onto-chan go-loop <!]])
            #?(:cljs [cljs.core.async :as a :refer [onto-chan <!]])
            #?(:clj [clojure.data.json :as json])
            #?(:cljs [goog.json]))
  #?(:cljs (:require-macros [cljs.core.async.macros :refer [go-loop]])))

(defn decode-json [s]
  "Convert JSON string to a list of maps"
  #?(
     :clj (json/read-str s)
     :cljs (-> s (goog.json/parse) (js->clj))))

(defn json-array-pipeline
  "Pipe from JSON array to maps. Input is a chan containing JSON strings.
   Output is a chan of maps."
  ([in out]
   (do
     (go-loop []
      (if-let [msg (<! in)]
        (let [msgs (decode-json msg)]
          (<! (onto-chan out msgs false))
          (recur))
        (a/close! out)))
     out)))
