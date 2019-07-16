(ns gilmour.migratus
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [migratus.core :as m]))

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

(defrecord Migratus [db-spec migration settings]
  c/Lifecycle
  (start [this]
    (let [source   (or (search-source this)
                       (throw (ex-info "migratus requires a source" {})))
          settings (assoc migration :db source)]
      (assoc this :settings settings)))
  (stop [this]
    (assoc this :settings nil)))

(defn migratus
  [config]
  (map->Migratus config))

(defn init!
  [migratus & args]
  (apply m/init (:settings migratus) args))

(defn migrate!
  [migratus]
  (m/migrate (:settings migratus)))

(defn rollback!
  [migratus]
  (m/migrate (:settings migratus)))

(defn up!
  [migratus & args]
  (apply m/up (:settings migratus) args))

(defn down!
  [migratus & args]
  (apply m/down (:settings migratus) args))

(defn create!
  [migratus & args]
  (apply m/create (:settings migratus) args))
