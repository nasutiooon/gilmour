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
      (assoc this :handler (middleware handler))))
  (stop [this]
    (assoc this :handler nil)))

(defn make-handler
  [opts]
  (map->Handler opts))
