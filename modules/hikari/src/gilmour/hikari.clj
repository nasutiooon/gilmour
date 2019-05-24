(ns gilmour.hikari
  (:require
   [hikari-cp.core :as hikari]
   [com.stuartsierra.component :as c]))

(defrecord Hikari [datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource this)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil)))

(defn make-hikari
  [config]
  (map->Hikari config))
