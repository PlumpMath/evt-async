(ns evt.worker
  (:use [clojure.core.async :only [go-loop <! timeout]]))

; Workers apply functions to all things in a channel
; and handle looping and throttling

(defn now []
  (System/currentTimeMillis))

(defn http-report [response]
  (let [{status       :status
         request-time :request-time} response
        x-rate-limit-reset (get-in response [:headers "X-Rate-Limit-Reset"])]
    (format "%d %dms X-Rate-Limit-Reset: %s"
            status
            request-time
            (if (nil? x-rate-limit-reset) "" x-rate-limit-reset ))))

(defn slow-worker [ch wrk]
  "Applies the wrk function to every thing in the channel,
   in series and with a minimum of one second between requests"
  (go-loop [t (now) ]
    (if-let [thng (<! ch)]
      (let [response   (wrk thng)
            elapsed-ms (- (now) t)]
        (println (http-report response))
        (if (< elapsed-ms 1000)
          (<! (timeout (- 1000 elapsed-ms)))
          (<! (timeout elapsed-ms)))
        (recur (now)))
      (println "Finished."))))

(defn fast-worker [ch wrk]
  "Applies the wrk function to every thing in the channel,
   in series and as fast as possible"
  (go-loop []
           (if-let [thng (<! ch)]
             (let [response (wrk thng)]
               (println (http-report response))
               (recur))
             (println "Finished."))))

