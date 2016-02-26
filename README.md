# EVT API

ClojureScript client API to the [Evrythng](https://evrythng.com) [IoT](https://en.wikipedia.org/wiki/Internet_of_Things) platform using [core.async](https://github.com/clojure/core.async).

An experiment to see if using channels results in more readable code than callbacks and promises.


# Examples

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