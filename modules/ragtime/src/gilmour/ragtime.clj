(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.hikari :as g.hikari]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defn- search-datastore
  [component]
  (let [ms (vals component)]
    (or (some->> ms
                 (filter (partial satisfies? g.hikari/SQLPool))
                 (map g.hikari/pool)
                 (first)
                 (hash-map :datasource))
        (->> ms
             (filter (partial satisfies? g.hikari/SQLSpec))
             (map g.hikari/db-spec)
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
