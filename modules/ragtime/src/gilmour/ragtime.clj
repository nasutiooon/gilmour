(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defrecord Ragtime [db config datastore migrations]
  c/Lifecycle
  (start [this]
    (let [datastore  (ragtime.j/load-resources (:path config))
          migrations (ragtime.j/sql-database
                      (if-let [datasource (:datasource db)]
                        {:datasource datasource}
                        (:config db)))]
      (assoc this :datastore datastore :migrations migrations)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn make-ragtime
  [config]
  (map->Ragtime {:config config}))

(defn migrate
  [ragtime]
  (ragtime.r/migrate ragtime))

(defn rollback
  [ragtime]
  (ragtime.r/rollback ragtime))
