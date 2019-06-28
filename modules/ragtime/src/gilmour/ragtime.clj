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
      (:db-spec component)))

(defrecord Ragtime [path db-spec datastore migrations]
  c/Lifecycle
  (start [this]
    (let [source (search-source this)]
      (if (and path source)
        (let [datastore  (ragtime.j/sql-database source)
              migrations (ragtime.j/load-resources path)]
          (assoc this :datastore datastore :migrations migrations))
        this)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn make-ragtime
  [config]
  (map->Ragtime config))

(defn migrate!
  [{:keys [datastore migrations] :as ragtime}]
  (when (and datastore migrations)
    (ragtime.r/migrate ragtime)))

(defn rollback!
  [{:keys [datastore migrations] :as ragtime}]
  (when (and datastore migrations)
    (ragtime.r/rollback ragtime)))
