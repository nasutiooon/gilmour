(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defn- search-source
  [component]
  (or (some->> (vals component)
               (filter (partial satisfies? g.sql/SQLPool))
               (map g.sql/pool)
               (first)
               (hash-map :datasource))
      (->> (vals component)
           (filter (partial satisfies? g.sql/SQLDb))
           (map g.sql/db-spec)
           (first))
      (:db-spec component)
      (or (throw (ex-info "need an active datasource or a db spec" {})))))

(defrecord Ragtime [migration-path db-spec datastore migrations]
  c/Lifecycle
  (start [this]
    (let [datastore  (ragtime.j/sql-database (search-source this))
          migrations (ragtime.j/load-resources migration-path)]
      (assoc this :datastore datastore :migrations migrations)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn ragtime
  [config]
  (map->Ragtime config))

(defn migrate!
  [ragtime]
  (ragtime.r/migrate ragtime))

(defn rollback!
  [ragtime]
  (ragtime.r/rollback ragtime))

(defn- search-ragtime
  [component]
  (->> (vals component)
       (filter (partial instance? Ragtime))
       (first)))

(defrecord RagtimeRunner []
  c/Lifecycle
  (start [this]
    (migrate! (search-ragtime this))
    this)
  (stop [this]
    this))

(defn ragtime-runner
  []
  (map->RagtimeRunner {}))
