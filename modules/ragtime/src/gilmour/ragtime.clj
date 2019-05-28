(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defn- search-datastore
  [component]
  (let [ms (vals component)]
    (or (some->> ms
                 (filter (partial satisfies? g.sql/SQLPool))
                 (map g.sql/pool)
                 (first)
                 (hash-map :datasource))
        (->> ms
             (filter (partial satisfies? g.sql/SQLSpec))
             (map g.sql/db-spec)
             (first)))))

(defrecord Ragtime [path datastore migrations]
  c/Lifecycle
  (start [this]
    (let [datastore  (ragtime.j/sql-database (search-datastore this))
          migrations (ragtime.j/load-resources path)]
      (assoc this :datastore datastore :migrations migrations)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn make-ragtime
  [config]
  (map->Ragtime config))

(defn migrate!
  [ragtime]
  (ragtime.r/migrate ragtime))

(defn rollback!
  [ragtime]
  (ragtime.r/rollback ragtime))
