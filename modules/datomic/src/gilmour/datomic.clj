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
  [config]
  (map->EphemeralDatomic config))

(defrecord DurableDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    this))

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
       (keep :uri)
       (first)))

(defrecord DatomicConnection [conn]
  c/Lifecycle
  (start [this]
    (let [uri (or (search-uri this)
                  (throw (ex-info "Can't find uri for Datomic" {})))]
      (assoc this :conn (d/connect (search-uri this)))))
  (stop [this]
    (when conn (d/release conn))
    (assoc this :conn nil)))

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
       (keep :conn)
       (first)))

(defn conform
  [{:keys [norm-map] :as datomic}]
  (ensure-conforms (search-conn datomic) norm-map))
