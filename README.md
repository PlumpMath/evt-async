# EVT API

ClojureScript client API to the [Evrythng](https://evrythng.com) [IoT](https://en.wikipedia.org/wiki/Internet_of_Things) platform using [core.async](https://github.com/clojure/core.async).

An experiment to see if using channels results in more readable code than callbacks and promises.

# Program Structure

The high-level structure of programs that use this library would be:
 
1. create a connection to an EVT account (determined by your Operator key)
2. call functions that query EVT thngs and return results on a channel
3. process the thngs on the channel 



# Examples

## Delete all tagged products

```clojure
(-> (evt.api/paginate
      evt.api/EVRYTHNG_API_KEY
      (evt.res/products-tagged "_TMP"))
    (evt.api/delete-all evt.res/product))
```
l


# Gotchas

The EVT API provide two mechanisms for working with large result sets:

* pagination (`perPage` and `page` URL parameters)
* [HTTP Link headers](https://www.w3.org/wiki/LinkHeader)

Be warned that if apply a filter to a query, and then follow the link to the next page of results,
then the filter is stripped from the second page. Therefore you will end up retrieving or deleting
thngs that you did not intend to delete.
