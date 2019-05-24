(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]))

(defn- search-handler
  [component]
  (or (:handler component)
      (->> (vals component)
           (keep :handler)
           (first))
      (throw (ex-info "aleph http server requires a handler" {}))))

(defrecord HttpServer [config server]
  c/Lifecycle
  (start [this]
    (let [handler (search-handler this)
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
