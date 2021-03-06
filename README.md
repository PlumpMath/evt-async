# EVT API

Clojure client API to the [Evrythng](https://evrythng.com) [IoT](https://en.wikipedia.org/wiki/Internet_of_Things) platform using [core.async](https://github.com/clojure/core.async).

An experiment to see if using channels results in more readable code than callbacks and promises.

# Program Structure

The high-level structure of programs that use this library would be:
 
1. create a connection to an EVT account (determined by your Operator key)
2. call functions that query EVT thngs and return results on a channel
3. process the thngs on the channel 


# Examples

## Echo the IDs of all Thngs

Show the IDs of all the Thngs in the account. We can use paginate as we are not using filters (see gotchas)

```clojure
(use 'evt.api)
(require '[evt.print :as p])

(let [evt (with-default-account)] 
  (-> 
    (evt paginate :thngs)
    (for-each p/echo-id)))
```

The `paginate` function returns a channel, and the `for-each` function consumes a channel as it's first argument.

## Tagged Products

We cannot use pagination, as we are using a filter. 

```clojure
(use 'evt.api)
(require '[evt.print :as p])
(require '[evt.filters :as f])

(let [evt (with-default-account)
      fltr (f/tagged "Not Applicable"] 
  (-> 
    (evt drain :products)
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
