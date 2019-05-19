(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]))

(defrecord HttpServer [config handler server]
  c/Lifecycle
  (start [this]
    (let [handler (:handler handler handler)
          server  (start-server handler config)]
      (assoc this :server server)))
  (stop [this]
    (when server (.close server))
    (assoc this :server nil)))

(defn make-http-server
  ([config]
   (map->HttpServer {:config config}))
  ([config handler]
   (map->HttpServer {:config config :handler handler})))
