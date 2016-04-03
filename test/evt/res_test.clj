(ns evt.res-test
  (:require [clojure.test :refer :all]
            [evt.res :refer :all]))

(deftest thngs-tagged-test
  (testing "thngs tagged"
    (is (= "https://api.evrythng.com/thngs?filter=tags=TMP"
           (thngs-tagged "TMP")))))

(deftest products-tagged-test
  (testing "products tagged"
    (is (= "https://api.evrythng.com/products?filter=tags=TMP&perPage=10"
           (products-tagged "TMP")))))
