(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]))

(defn- default-handler
  [_]
  {:status 200
   :body   "No request handler found in `gilmour.aleph.HttpServer`"})

(defn- search-handler
  [component]
  (->> (vals component)
       (keep :handler)
       (first)))

(defrecord HttpServer [config server]
  c/Lifecycle
  (start [this]
    (let [handler (or (search-handler this) default-handler)
          server  (start-server handler config)]
      (assoc this :server server)))
  (stop [this]
    (when server (.close server))
    (assoc this :server nil)))

(defn make-http-server
  [config opts]
  (-> opts
      (assoc :config config)
      (map->HttpServer)))
