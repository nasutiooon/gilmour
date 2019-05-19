(ns gilmour.datomic
  (:require
   [com.stuartsierra.component :as c]
   [datomic.api :as d]))

(defrecord EphemeralDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    (d/delete-database uri)
    this))

(defn make-ephemeral-datomic
  [uri]
  (map->EphemeralDatomic {:uri uri}))

(defrecord DurableDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    this))

(defn make-durable-datomic
  [uri]
  (map->DurableDatomic {:uri uri}))

(defrecord Datomic [impl conn]
  c/Lifecycle
  (start [this]
    (assoc this :conn (d/connect (:uri impl))))
  (stop [this]
    (when conn (d/release conn))
    (assoc this :conn nil)))

(defn make-datomic
  []
  (map->Datomic {}))
