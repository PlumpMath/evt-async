(ns evrythng.worker-test
  (:use [evt.worker :as w])
  (:require [clojure.test :refer :all]))


(deftest http-report-test
  (testing "http-report without limit"
    (let [res {:status 401,
               :headers {"Access-Control-Allow-Origin" "*",
                         "Access-Control-Expose-Headers" "Link, X-Result-Count",
                         "Content-Type" "application/json",
                         "Date" "Sun, 03 Apr 2016 12:02:20 GMT",
                         "transfer-encoding" "chunked",
                         "Connection" "Close"},
               :body "{\"status\":401,\"errors\":[\"Access denied, please check your credentials!\"],\"code\":33619968,\"moreInfo\":\"https://dashboard.evrythng.com/developers/apidoc/authentication\"}",
               :request-time 1024,
               :trace-redirects ["https://api.evrythng.com/thngs"],
               :orig-content-encoding nil}]
       (is
         (= "401 1024ms X-Rate-Limit-Reset: " (w/http-report res))))))