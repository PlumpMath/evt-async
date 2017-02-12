(ns evrythng.net-test
  (:require [clojure.test :refer :all]
            [evrythng.net :refer :all]))

(deftest auth-test
  (testing "Authorization header"
    (is (= {:headers {"Authorization" "T0KEN"}}
           (auth-header {} "T0KEN")))))

(deftest json-test
  (testing "JSON headers"
    (is (= {:accept :json
            :as :json}
           (json-header {})))))
