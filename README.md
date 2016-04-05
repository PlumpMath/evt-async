# EVT API

ClojureScript client API to the [Evrythng](https://evrythng.com) [IoT](https://en.wikipedia.org/wiki/Internet_of_Things) platform using [core.async](https://github.com/clojure/core.async).

An experiment to see if using channels results in more readable code than callbacks and promises.


# Examples

## Find all tagged products with word in description

Use a filter to get all Products tagged _Glass_, then use a
transducer to get only those with _GREEN_ in the description

```clojure
(use 'evt.api)
(use 'evt.res)
(require '[evt.query :as q])

(-> 
   (evt.api/query 
      evt.api/EVRYTHNG_API_KEY 
      (evt.res/products-tagged "Glass")
      (q/description-contains "GREEN"))
   (evt.api/echo))
```

```clojure
(use 'evt.api)
(use 'evt.res)
(require '[evt.filters :as f])
(require '[evt.query :as q])

(-> 
   (evt.api/query 
      evt.api/EVRYTHNG_API_KEY 
      evt.res/products-url
      (f/tagged "Glass")
      (q/description-contains "GREEN"))
   (evt.api/echo))
```

```clojure
(use 'evt.api)
(use 'evt.res)
(require '[evt.filters :as f])
(require '[evt.query :as q])

(-> 
   (evt.api/query 
      evt.api/EVRYTHNG_API_KEY 
      evt.res/products-url
      (f/in-project (f/tagged "Cork") "UDnkqspYQfdN6DhgXBkMhmkh")
      (q/description-contains ""))
   (evt.api/echo))
```


```clojure
(use 'evt.api)
(use 'evt.res)
(require '[evt.filters :as f])
(require '[evt.query :as q])
(require '[evt.net :as n])


(defn set-photo [photo-url thng]
  (let [id (:id thng)
        url (evt.res/product id)
        body {:photos [photo-url]}]
        (n/put-json evt.api/EVRYTHNG_API_KEY url body)))

(-> 
   (evt.api/query 
      evt.api/EVRYTHNG_API_KEY 
      evt.res/products-url
      (f/in-project (f/tagged "Cork") "UDnkqspYQfdN6DhgXBkMhmkh")
      (q/description-contains ""))
   (evt.api/for-each (partial set-photo CORK)))
```



## Delete all tagged products

```clojure
(-> (evt.api/paginate
      evt.api/EVRYTHNG_API_KEY
      (evt.res/products-tagged "_TMP"))
    (evt.api/delete-all evt.res/product))
```

Temporary grammar to run multiple consumers:

```clojure
(let [ch (evt.api/paginate
      evt.api/EVRYTHNG_API_KEY
      (evt.res/products-tagged "BPCS/LX"))]
    (evt.api/delete-all ch evt.res/product)
    (evt.api/delete-all ch evt.res/product))
```