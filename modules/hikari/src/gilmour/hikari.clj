(ns gilmour.hikari
  (:require
   [com.stuartsierra.component :as c]
   [gilmour.sql :as g.sql]
   [hikari-cp.core :as hikari]))

(defrecord Hikari [pool-spec datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource pool-spec)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil))

  g.sql/SQLPool
  (pool [_] datasource)
  (pool-spec [_] pool-spec))

(defn make-hikari
  [config]
  (map->Hikari config))
