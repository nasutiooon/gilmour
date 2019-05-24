(ns gilmour.handler
  (:require
   [com.stuartsierra.component :as c]))

(defn- search-wrapper
  [component]
  (->> (vals component)
       (keep :wrapper)
       (first)))

(defn- search-handler
  [component]
  (->> (vals component)
       (keep :handler)
       (first)))

(defrecord Handler [handler]
  c/Lifecycle
  (start [this]
    (let [wrapper (or (search-wrapper this) identity)
          handler (or (search-handler this)
                      (throw (ex-info "Can't find handler for Handler" {})))]
      (assoc this :handler (wrapper handler))))
  (stop [this]
    (assoc this :handler nil)))

(defn make-handler
  []
  (map->Handler {}))
