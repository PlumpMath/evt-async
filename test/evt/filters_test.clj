(ns evt.filters-test
  (:require [evt.filters :as f])
  (:require [clojure.test :refer :all]))


(deftest filter-pagination-test
  (testing "Pagination"
    (is (= {:page 2}
           (f/page {} 2)))
        (is (= {:page 2}
           (f/next-page {} )))
        (is (= {:page 3}
           (f/next-page {:page 2} )))))

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

(deftest filters-test
  (testing "Combined filters"
    (let [expected "perPage=30&project=UDnkqspYQfdN6DhgXBkMhmkh&filter=tags=Cork"]
      (is 
        (= expected 
          (-> 
            (f/tagged "Cork") 
            (f/in-project "UDnkqspYQfdN6DhgXBkMhmkh") 
            (f/params)))))))

