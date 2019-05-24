(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]))

(defn- search-handler
  [component]
  (->> (vals component)
       (keep :handler)
       (first)))

(defrecord HttpServer [server]
  c/Lifecycle
  (start [this]
    (let [handler (or (search-handler this)
                      (throw (ex-info "aleph http server requires a handler" {})))
          server  (start-server handler this)]
      (assoc this :server server)))
  (stop [this]
    (when server (.close server))
    (assoc this :server nil)))

(defn make-http-server
  [config]
  (map->HttpServer config))
