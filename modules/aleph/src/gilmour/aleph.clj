(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]))

(defn- default-handler
  [_]
  {:status 200
   :body   "No request handler found in `gilmour.aleph.HttpServer`"})

(defrecord HttpServer [config handler server]
  c/Lifecycle
  (start [this]
    (let [handler (:handler handler default-handler)
          server  (start-server handler config)]
      (assoc this :server server)))
  (stop [this]
    (when server (.close server))
    (assoc this :server nil)))

(defn make-http-server
  ([config]
   (map->HttpServer {:config config}))
  ([config handler]
   (map->HttpServer {:config config :handler {:handler handler}})))
