(ns gilmour.ragtime
  (:require
   [com.stuartsierra.component :as c]
   [ragtime.jdbc :as ragtime.j]
   [ragtime.repl :as ragtime.r]))

(defn- search-datastore
  [component]
  (or (some->> (vals component)
               (keep :datasource)
               (first)
               (hash-map :datasource))
      (->> (vals component)
           (keep :config)
           (first))))

(defrecord Ragtime [config datastore migrations]
  c/Lifecycle
  (start [this]
    (let [datastore  (ragtime.j/sql-database (search-datastore this))
          migrations (ragtime.j/load-resources (:path config))]
      (assoc this :datastore datastore :migrations migrations)))
  (stop [this]
    (assoc this :datastore nil :migrations nil)))

(defn make-ragtime
  [config]
  (map->Ragtime {:config config}))

(defn migrate!
  [ragtime]
  (ragtime.r/migrate ragtime))

(defn rollback!
  [ragtime]
  (ragtime.r/rollback ragtime))
