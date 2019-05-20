(ns gilmour.datomic
  (:require
   [clojure.edn :as edn]
   [com.stuartsierra.component :as c]
   [datomic.api :as d]
   [io.rkn.conformity :refer [ensure-conforms]]))

(defrecord EphemeralDatomic [config]
  c/Lifecycle
  (start [this]
    (d/create-database (:uri config))
    this)
  (stop [this]
    (d/delete-database (:uri config))
    this))

(defn make-ephemeral-datomic
  [config]
  (map->EphemeralDatomic {:config config}))

(defrecord DurableDatomic [config]
  c/Lifecycle
  (start [this]
    (d/create-database (:uri config))
    this)
  (stop [this]
    this))

(defn make-durable-datomic
  [config]
  (map->DurableDatomic {:config config}))

(defn make-datomic
  [{:keys [temporary?] :as config}]
  (if temporary?
    (make-ephemeral-datomic config)
    (make-durable-datomic config)))

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

(defrecord DatomicConformer [config db norm-map]
  c/Lifecycle
  (start [this]
    (let [norm-map (-> config :path slurp edn/read-string)]
      (assoc this :norm-map norm-map)))
  (stop [this]
    (assoc this :resut nil)))

(defn make-datomic-conformer
  [config]
  (map->DatomicConformer {:config config}))

(defn conform
  [{:keys [db norm-map]}]
  (ensure-conforms (:conn db) norm-map))
