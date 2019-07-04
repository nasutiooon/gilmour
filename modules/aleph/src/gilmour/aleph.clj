(ns gilmour.aleph
  (:require
   [aleph.http :refer [start-server]]
   [com.stuartsierra.component :as c]
   [gilmour.ring :as g.ring]))

(defn- search-handler
  [component]
  (or (when-let [handler (:handler component)]
        (g.ring/request-handler handler))
      (->> (vals component)
           (filter (partial satisfies? g.ring/RequestHandler))
           (map g.ring/request-handler)
           (first))))

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

(defn http-server
  [config]
  (map->HttpServer config))
