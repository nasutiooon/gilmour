(ns gilmour.postgres
  (:require
   [gilmour.sql :as g.sql]
   [jdbc.core :as j]))

(defrecord Postgres [])

(defn postgres
  []
  (map->Postgres {}))

(defn error-code
  [ex]
  (.getSQLState (.getServerErrorMessage ex)))

(defn pool-spec->db-spec
  [{:keys [adapter server-name port-number database-name username password]}]
  (cond-> {:subprotocol adapter
           :subname     (str "//" server-name ":" port-number "/" database-name)}
    username (assoc :user username)
    password (assoc :password password)))

(defn pool-spec->postgres-db-spec
  [spec]
  (pool-spec->db-spec (assoc spec :database-name "postgres")))

(defrecord PostgresGenerator [pool-spec])

(defn postgres-generator
  [config]
  (map->PostgresGenerator config))

(defn create!
  [{:keys [pool-spec]}]
  (with-open [conn (j/connection (pool-spec->postgres-db-spec pool-spec))]
    (let [db-name (:database-name pool-spec)]
      (j/execute conn (str "DROP DATABASE IF EXISTS " db-name))
      (j/execute conn (str "CREATE DATABASE " db-name)))))

(defn destroy!
  [{:keys [pool-spec]}]
  (with-open [conn (j/connection (pool-spec->postgres-db-spec pool-spec))]
    (let [db-name (:database-name pool-spec)]
      (j/execute conn (str "DROP DATABASE " db-name)))))
