(require '[clj-http.client :as h])

(def EVRYTHNG_API_KEY (System/getenv "EVRYTHNG_API_KEY"))

(-> 
	(slurp "")
	(line-seq)
	(count))



(def expected #{200 404})

(defn del [l]
	(let [l (.replace l "gg/" "gg/redirections/")
     	  result (h/delete l {:headers {"Authorization" EVRYTHNG_API_KEY} :throw-exceptions false})]
     		(if (contains? expected (:status result))
		    	(println l)
		    	(println result))))



(->>
	(slurp "/Users/jaev/Desktop/kill_redirections_b.txt")
	(clojure.string/split-lines)
    (pmap del))



