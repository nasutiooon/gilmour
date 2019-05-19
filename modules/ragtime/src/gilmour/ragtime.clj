(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defrecord Ragtime [db-spec datasource path datastore migrations]
  c/Lifecycle
  (start [this]
    (let [datastore  (ragtime.j/load-resources path)
          migrations (ragtime.j/sql-database (or datasource db-spec))]
      (assoc this :datastore datastore :migrations migrations)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn make-ragtime
  [config]
  (map->Ragtime config))

(defn migrate
  [ragtime]
  (ragtime.r/migrate ragtime))

(defn rollback
  [ragtime]
  (ragtime.r/rollback ragtime))
