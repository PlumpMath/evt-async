(ns evrythng.resources
  "Resource URLs of the EVRYTHNG API"
  (:require [evrythng.client :as client]))

(def API-EVT-COM "https://api.evrythng.com")

(def API-EVT-EU "https://api-eu.evrythng.com")

(def regions {:com API-EVT-COM
              :eu  API-EVT-EU})

(defmacro with-api
  "Context that supplies the base URL and the API Key
   as the first paramters of each API request. Returns
   the value of the last form."
  [key region & forms]
  (let [base-region-url (get regions region API-EVT-COM)]
    `(do
       ~@(map (fn [f]
                (if (seq? f)
                  `(~(first f) ~key ~base-region-url ~@(next f))))
              forms))))

;(<!! (evt/with-api (System/getenv "EVT_OPERATOR_KEY") :com
;                   (evt/actions-tagged "_SET" "865-29115")))

(defn actions-tagged [key base action tag]
  (let [url (format "%s/actions/%s?filter=tags=%s" base action tag)]
    (println key)
    (println url)
    (client/get-resource key url)))
