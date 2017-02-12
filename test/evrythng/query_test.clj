(ns evrythng.query-test
  (:use [evrythng.query :as q])
  (:require [clojure.test :refer :all]))

; TODO how to test?
;(deftest test-description-contains
;  (testing "Descriptions contains"
;    (let [input [{:description "" }
;                 {:description "target"}
;                 {:description "before target"}
;                 {:description "before TARGET after"}
;                 {:description "before other after"}]
;          expected [{:description "target"}
;                    {:description "before target"}
;                    {:description "before TARGET after"}]]
;      (is
;        (= expected (into [] q/description-contains input))))))
