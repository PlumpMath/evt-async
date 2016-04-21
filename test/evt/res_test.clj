(ns evt.res-test
  (:import [java.net URL])
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

(deftest resource-url-test
  (let [EVT-API (URL. "https://api.evrythng.com")]
    (testing "Common resouces"
      (is (= "https://api.evrythng.com/products" (resource-url EVT-API :products)))
      (is (= "https://api.evrythng.com/thngs" (resource-url EVT-API :thngs))))))

