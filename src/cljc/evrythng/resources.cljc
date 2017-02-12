(ns evrythng.resources
  "Resource URLs of the EVRYTHNG API")

(def API-EVT-COM "https://api.evrythng.com")

(def API-EVT-EU  "https://api-eu.evrythng.com")

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