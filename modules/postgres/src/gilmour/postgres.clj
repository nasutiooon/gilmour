(ns gilmour.postgres
  (:require
   [clojure.string :as string]
   [gilmour.sql :as g.sql]
   [jdbc.core :as j]))

(defrecord PGPool []
  g.sql/SQLDb
  (db-spec [this]
    (->> (vals this)
         (filter (partial satisfies? g.sql/SQLDb))
         (map g.sql/db-spec)
         (first))))

(defn pg-pool
  []
  (map->PGPool {}))

(defn error-code
  [ex]
  (.getSQLState (.getServerErrorMessage ex)))

(defn- get-db-name
  [db-spec]
  (let [splitted (some-> db-spec :subname (string/split #"\/"))]
    (when (= (count splitted) 4)
      (last splitted))))

(defn- pg-db-spec
  [db-spec]
  (let [splitted (some-> db-spec :subname (string/split #"\/"))]
    (when (= (count splitted) 4)
      (assoc db-spec :subname (-> (take 3 splitted)
                                  (vec)
                                  (conj "postgres")
                                  (as-> <> (string/join "/" <>)))))))

(defrecord PGGenerator [db-spec])

(defn pg-generator
  [config]
  (map->PGGenerator config))

(defn create!
  [{:keys [db-spec]}]
  (with-open [conn (j/connection (pg-db-spec db-spec))]
    (let [db-name (get-db-name db-spec)]
      (when (nil? (j/fetch-one conn (str "SELECT datname "
                                         "FROM pg_catalog.pg_database "
                                         "WHERE datname = '"
                                         db-name
                                         "'")))
        (j/execute conn (str "CREATE DATABASE " db-name))))))

(defn create-fresh!
  [{:keys [db-spec]}]
  (with-open [conn (j/connection (pg-db-spec db-spec))]
    (let [db-name (get-db-name db-spec)]
      (j/execute conn (str "DROP DATABASE IF EXISTS " db-name))
      (j/execute conn (str "CREATE DATABASE " db-name)))))

(defn destroy!
  [{:keys [db-spec]}]
  (with-open [conn (j/connection (pg-db-spec db-spec))]
    (j/execute conn (str "DROP DATABASE IF EXISTS " (get-db-name db-spec)))))
