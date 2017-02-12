(ns evrythng.json-test
  (:require [clojure.test :refer :all]
            [evrythng.json :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.core.async :as a :refer [<!! chan]]))


(defn vector-to-json [xi]
  (->> xi
       (clojure.string/join ", ")
       (format "[%s]")))

(defspec test-json-on-channels
         (prop/for-all [xi (gen/vector gen/int)]
                       (let [in (chan)
                             out (chan (max (count xi) 1))]
                         (a/put! in (vector-to-json xi))
                         (json-array-pipeline in out)
                         (a/close! in)
                         (let [results (a/<!! (a/into [] out))]
                           (is (= xi results))))))
