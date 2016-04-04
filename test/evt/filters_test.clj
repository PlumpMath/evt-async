(ns evt.filters-test
  (:use [evt.filters :as f])
  (:require [clojure.test :refer :all]))


(deftest filter-pagination-test
  (testing "Pagination"
    (is (= {:page 2}
           (f/page {} 2)))))

(deftest filter-in-project-test
  (testing "In Project"
    (is (= {:project "XYZ"}
           (f/in-project {} "XYZ")))))

(deftest filter-tag-test
  (testing "First tag"
    (is (= {:tags [:a]}
           (f/tagged {} :a))))
  (testing "Second tag"
    (is (= {:tags [:a :b]}
           (f/tagged {:tags [:a]} :b))))
  (testing "Tag"
    (is (= {:tags [:c]}
           (f/tagged {} :c)))))

