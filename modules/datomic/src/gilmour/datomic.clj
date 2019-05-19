(ns gilmour.datomic
  (:require
   [clojure.edn :as edn]
   [com.stuartsierra.component :as c]
   [datomic.api :as d]
   [io.rkn.conformity :refer [ensure-conforms]]))

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

(defn make-datomic
  [{:keys [uri temporary?]}]
  (if temporary?
    (make-ephemeral-datomic uri)
    (make-durable-datomic uri)))

(defrecord DatomicConnection [impl conn]
  c/Lifecycle
  (start [this]
    (assoc this :conn (d/connect (:uri impl))))
  (stop [this]
    (when conn (d/release conn))
    (assoc this :conn nil)))

(defn make-datomic-connection
  []
  (map->DatomicConnection {}))

(defrecord DatomicConformer [datomic path result]
  c/Lifecycle
  (start [this]
    (let [norm-map (-> path slurp edn/read-string)
          result   (ensure-conforms (:conn datomic) norm-map)]
      (assoc this :result result)))
  (stop [this]
    (assoc this :resut nil)))

(defn make-datomic-conformer
  [path]
  (map->DatomicConformer {:path path}))
