(ns gilmour.handler
  (:require
   [com.stuartsierra.component :as c]))

(defn- default-handler
  [_]
  {:status 200
   :body   "No request handler found in `gilmour.handler.Handler`"})

(defrecord Handler [request-middleware request-handler handler]
  c/Lifecycle
  (start [this]
    (let [middleware (:middleware request-middleware identity)
          handler    (:handler request-handler default-handler)]
      (middleware handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn make-handler
  ([handler middleware]
   (map->Handler {:request-handler    {:handler handler}
                  :request-middleware {:middlware middleware}}))
  ([handler]
   (map->Handler {:request-handler {:handler handler}}))
  ([]
   (map->Handler {})))