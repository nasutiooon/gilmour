(ns gilmour.hikari
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [hikari-cp.core :as hikari]))

(defrecord Hikari [db-spec datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource db-spec)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil))

  g.sql/SQLPool
  (pool [_] datasource)

  g.sql/SQLSpec
  (db-spec [_] db-spec))

(defn make-hikari
  [config]
  (map->Hikari config))
