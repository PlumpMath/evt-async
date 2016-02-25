(defproject evt.api "0.1.0"
  :description "EVT API using core.async"
  :url "https://github.com/devstopfix/evt-async"
  :license {:name "MIT License"
            :url "https://en.wikipedia.org/wiki/MIT_License"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.2.374"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]
                 [org.clojure/tools.logging "0.3.1"]])