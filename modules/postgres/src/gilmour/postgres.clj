(ns gilmour.postgres
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [jdbc.core :as j]))

(defrecord PGPool []
  g.sql/SQLPool
  (pool [this]
    (->> (vals this)
         (filter (partial satisfies? g.sql/SQLPool))
         (map g.sql/pool)
         (first)))
  (pool-spec [this]
    (->> (vals this)
         (filter (partial satisfies? g.sql/SQLPool))
         (map g.sql/pool-spec)
         (first))))

(defn pg-pool
  []
  (map->PGPool {}))

(defn error-code
  [ex]
  (.getSQLState (.getServerErrorMessage ex)))

(defn db-spec
  [{:keys [adapter server-name port-number database-name username password]}]
  (cond-> {:subprotocol adapter
           :subname     (str "//" server-name ":" port-number "/" database-name)}
    username (assoc :user username)
    password (assoc :password password)))

(defn pg-db-spec
  [spec]
  (db-spec (assoc spec :database-name "postgres")))

(defrecord DurablePGGenerator [pool-spec]
  c/Lifecycle
  (start [this]
    (with-open [conn (j/connection (pg-db-spec pool-spec))]
      (let [db-name (:database-name pool-spec)]
        (when (nil? (j/fetch-one conn (str "SELECT datname "
                                           "FROM pg_catalog.pg_database "
                                           "WHERE datname = '"
                                           db-name
                                           "'")))
          (j/execute conn (str "CREATE DATABASE " db-name)))
        this)))
  (stop [this]
    this))

(defn durable-pg-generator
  [config]
  (map->DurablePGGenerator config))

(defrecord EphemeralPGGenerator [pool-spec]
  c/Lifecycle
  (start [this]
    (with-open [conn (j/connection (pg-db-spec pool-spec))]
      (let [db-name (:database-name pool-spec)]
        (j/execute conn (str "DROP DATABASE IF EXISTS " db-name))
        (j/execute conn (str "CREATE DATABASE " db-name)))
      this))
  (stop [this]
    (with-open [conn (j/connection (pg-db-spec pool-spec))]
      (j/execute conn (str "DROP DATABASE " (:database-name pool-spec)))
      this)))

(defn ephemeral-pg-generator
  [config]
  (map->EphemeralPGGenerator config))

(defn pg-generator
  [{:keys [temporary?] :as config}]
  (if temporary?
    (ephemeral-pg-generator config)
    (durable-pg-generator config)))
