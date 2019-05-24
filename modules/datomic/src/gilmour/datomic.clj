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

(defrecord DatomicConformer [config norm-map]
  c/Lifecycle
  (start [this]
    (let [norm-map (-> config :path slurp edn/read-string)]
      (assoc this :norm-map norm-map)))
  (stop [this]
    (assoc this :resut nil)))

(defn make-datomic-conformer
  [config]
  (map->DatomicConformer {:config config}))

(defn- search-conn
  [component]
  (->> (vals component)
       (keep :conn)
       (first)))

(defn conform
  [{:keys [norm-map] :as datomic}]
  (let [conn (or (search-conn datomic)
                 (throw (ex-info "Can't find Datomic connection" {})))]
    (ensure-conforms conn norm-map)))
