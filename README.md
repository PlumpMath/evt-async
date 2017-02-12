# EVT API

Clojure/ClojureScript async client API to the [Evrythng](https://evrythng.com) [IoT](https://en.wikipedia.org/wiki/Internet_of_Things) platform using [core.async](https://github.com/clojure/core.async).

An experiment to see if using channels results in more readable code than callbacks and promises.


# REPL

Get a page of Thngs:

```
(require '[clojure.core.async :as a :refer :all])

(let [key (System/getenv "EVT_OPERATOR_KEY")]
  (go
    (let [e (a/chan)
          c (evrythng.client/get key "https://api.evrythng.com/thngs?perPage=2" e)]
        (clojure.pprint/pprint
          (a/alts! [c e (a/timeout 1000)])))))
```






# Program Structure

The high-level structure of programs that use this library would be:
 
1. create a connection to an EVT account (determined by your Operator key)
2. call functions that query EVT thngs and return results on a channel
3. process the thngs on the channel 


# Examples

## Echo the IDs of all Thngs

Show the IDs of all the Thngs in the account. We can use paginate as we are not using filters (see gotchas)

```clojure
(use 'evrythng.api)
(require '[evrythng.print :as p])

(let [evrythng (with-default-account)]
  (-> 
    (evrythng paginate :thngs)
    (for-each p/echo-id)))
```

The `paginate` function returns a channel, and the `for-each` function consumes a channel as it's first argument.

## Tagged Products

We cannot use pagination, as we are using a filter. 

```clojure
(use 'evrythng.api)
(require '[evrythng.print :as p])
(require '[evrythng.filters :as f])

(let [evrythng (with-default-account)
      fltr (f/tagged "Not Applicable"] 
  (-> 
    (evrythng drain :products)
    (for-each p/echo-tags)))
```


# Gotchas

The EVT API provide two mechanisms for working with large result sets:

* pagination (`perPage` and `page` URL parameters)
* [HTTP Link headers](https://www.w3.org/wiki/LinkHeader)

Be warned that if apply a filter to a query, and then follow the link to the next page of results,
then the filter is stripped from the second page. Therefore you will end up retrieving or deleting
thngs that you did not intend to delete.




# OLD

## Find all tagged products with word in description

Use a filter to get all Products tagged _Glass_, then use a
transducer to get only those with _GREEN_ in the description

```clojure
(use 'evrythng.api)
(use 'evrythng.res)
(require '[evrythng.query :as q])

(-> 
   (evrythng.api/query
      evrythng.api/EVRYTHNG_API_KEY
      (evrythng.res/products-tagged "Glass")
      (q/description-contains "GREEN"))
   (evrythng.api/echo))
```

```clojure
(use 'evrythng.api)
(use 'evrythng.res)
(require '[evrythng.filters :as f])
(require '[evrythng.query :as q])

(-> 
   (evrythng.api/query
      evrythng.api/EVRYTHNG_API_KEY
      evrythng.res/products-url
      (f/tagged "Glass")
      (q/description-contains "GREEN"))
   (evrythng.api/echo))
```

```clojure
(use 'evrythng.api)
(use 'evrythng.res)
(require '[evrythng.filters :as f])
(require '[evrythng.query :as q])

(-> 
   (evrythng.api/query
      evrythng.api/EVRYTHNG_API_KEY
      evrythng.res/products-url
      (f/in-project (f/tagged "Cork") "UDnkqspYQfdN6DhgXBkMhmkh")
      (q/description-contains ""))
   (evrythng.api/echo))
```


```clojure
(use 'evrythng.api)
(use 'evrythng.res)
(require '[evrythng.filters :as f])
(require '[evrythng.query :as q])
(require '[evrythng.net :as n])


(defn set-photo [photo-url thng]
  (let [id (:id thng)
        url (evrythng.res/product id)
        body {:photos [photo-url]}]
        (n/put-json evrythng.api/EVRYTHNG_API_KEY url body)))

(-> 
   (evrythng.api/query
      evrythng.api/EVRYTHNG_API_KEY
      evrythng.res/products-url
      (f/in-project (f/tagged "Cork") "UDnkqspYQfdN6DhgXBkMhmkh")
      (q/description-contains ""))
   (evrythng.api/for-each (partial set-photo CORK)))
```


## Delete all tagged products

```clojure
(-> (evrythng.api/paginate
      evrythng.api/EVRYTHNG_API_KEY
      (evrythng.res/products-tagged "_TMP"))
    (evrythng.api/delete-all evrythng.res/product))
```
