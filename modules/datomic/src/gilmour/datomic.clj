(ns gilmour.datomic
  (:require
   [clojure.edn :as edn]
   [com.stuartsierra.component :as c]
   [datomic.api :as d]
   [io.rkn.conformity :refer [ensure-conforms]]))

(defprotocol DatomicBlueprint
  (uri [this]))

(defprotocol Datomic
  (conn [this]))

(defrecord EphemeralDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    (d/delete-database uri)
    this)

  DatomicBlueprint
  (uri [_] uri))

(defn make-ephemeral-datomic
  [config]
  (map->EphemeralDatomic config))

(defrecord DurableDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    this)

  DatomicBlueprint
  (uri [_] uri))

(defn make-durable-datomic
  [config]
  (map->DurableDatomic config))

(defn make-datomic
  [{:keys [temporary?] :as config}]
  (if temporary?
    (make-ephemeral-datomic config)
    (make-durable-datomic config)))

(defn- search-uri
  [component]
  (->> (vals component)
       (filter (partial satisfies? DatomicBlueprint))
       (map uri)
       (first)))

(defrecord DatomicConnection [conn]
  c/Lifecycle
  (start [this]
    (let [uri (or (search-uri this)
                  (throw
                   (ex-info "Datomic connection requires a blueprint" {})))]
      (assoc this :conn (d/connect uri))))
  (stop [this]
    (when conn (d/release conn))
    (assoc this :conn nil))

  Datomic
  (conn [_] conn))

(defn make-datomic-connection
  []
  (map->DatomicConnection {}))

(defrecord DatomicConformer [path norm-map]
  c/Lifecycle
  (start [this]
    (let [norm-map (-> path slurp edn/read-string)]
      (assoc this :norm-map norm-map)))
  (stop [this]
    (assoc this :norm-map nil)))

(defn make-datomic-conformer
  [config]
  (map->DatomicConformer config))

(defn- search-conn
  [component]
  (->> (vals component)
       (filter (partial satisfies? Datomic))
       (map conn)
       (first)))

(defn conform
  [{:keys [norm-map] :as datomic}]
  (ensure-conforms (search-conn datomic) norm-map))
