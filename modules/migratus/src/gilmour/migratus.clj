(ns gilmour.migratus
  (:require
   [gilmour.sql :as g.sql]
   [migratus.core :as m]))

(defn- get-settings
  [{:keys [db-spec migration] :as component}]
  (let [db-spec (or (->> (vals component)
                         (filter (partial satisfies? g.sql/SQLDb))
                         (map g.sql/db-spec)
                         (first))
                    db-spec)]
    (cond-> migration
      db-spec (assoc :db db-spec))))

(defrecord Migratus [])

(defn migratus
  [config]
  (map->Migratus config))

(defn init!
  [migratus & args]
  (apply m/init (get-settings migratus) args))

(defn migrate!
  [migratus]
  (m/migrate (get-settings migratus)))

(defn rollback!
  [migratus]
  (m/migrate (get-settings migratus)))

(defn up!
  [migratus & args]
  (apply m/up (get-settings migratus) args))

(defn down!
  [migratus & args]
  (apply m/down (get-settings migratus) args))

(defn create!
  [migratus & args]
  (apply m/create (get-settings migratus) args))
