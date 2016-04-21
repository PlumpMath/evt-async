(ns evt.api-test
  (:require [clojure.test :refer :all]
            [evt.api :refer :all]))


(deftest with-account-test
  (testing "We can pass a key to an account"
    (let [mock-action (fn [key url ] (.toUpperCase key))
          evt (with-account "OurKEy")]
      (is (= "OURKEY" (evt mock-action :any))))))
