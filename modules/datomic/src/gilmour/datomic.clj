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

(defn ephemeral-datomic
  [config]
  (map->EphemeralDatomic config))

(defrecord DurableDatomic [uri]
  c/Lifecycle
  (start [this]
    (d/create-database uri)
    this)
  (stop [this]
    this))

(defn durable-datomic
  [config]
  (map->DurableDatomic config))

(defn datomic
  [{:keys [temporary?] :as config}]
  (if temporary?
    (ephemeral-datomic config)
    (durable-datomic config)))

(defn- search-uri
  [component]
  (->> (vals component)
       (filter #(or (instance? EphemeralDatomic %)
                    (instance? DurableDatomic %)))
       (map :uri)
       (first)))

(defrecord DatomicConnection [conn]
  c/Lifecycle
  (start [this]
    (let [uri (or (search-uri this)
                  (throw (ex-info "datomic conn requires a blueprint" {})))]
      (assoc this :conn (d/connect uri))))
  (stop [this]
    (when conn (d/release conn))
    (assoc this :conn nil)))

(defn datomic-connection
  []
  (map->DatomicConnection {}))

(defrecord DatomicConformer [path norm-map]
  c/Lifecycle
  (start [this]
    (let [norm-map (-> path slurp edn/read-string)]
      (assoc this :norm-map norm-map)))
  (stop [this]
    (assoc this :norm-map nil)))

(defn datomic-conformer
  [config]
  (map->DatomicConformer config))

(defn- search-conn
  [component]
  (->> (vals component)
       (filter (partial instance? DatomicConnection))
       (map :conn)
       (first)))

(defn conform!
  [{:keys [norm-map] :as datomic}]
  (ensure-conforms
   (or (search-conn datomic)
       (throw (ex-info "conforming requires a datomic conn" {})))
   norm-map))

(defn- search-datomic-conformer
  [component]
  (->> (vals component)
       (filter (partial instance? DatomicConformer))
       (first)))

(defrecord DatomicConformerRunner []
  c/Lifecycle
  (start [this]
    (conform!
     (or (search-datomic-conformer this)
         (throw (ex-info "datomic conformer runner requires a datomic conformer" {}))))
    this)
  (stop [this]
    this))

(defn datomic-conformer-runner
  []
  (map->DatomicConformerRunner {}))
