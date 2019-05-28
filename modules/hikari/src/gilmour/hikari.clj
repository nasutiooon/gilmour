(ns gilmour.hikari
  (:require
   [hikari-cp.core :as hikari]
   [com.stuartsierra.component :as c]))

(defprotocol SQLPool
  (pool [this]))

(defprotocol SQLSpec
  (db-spec [this]))

(defrecord Hikari [db-spec datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource db-spec)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil))

  SQLPool
  (pool [_] datasource)

  SQLSpec
  (db-spec [_] db-spec))

(defn make-hikari
  [config]
  (map->Hikari config))
