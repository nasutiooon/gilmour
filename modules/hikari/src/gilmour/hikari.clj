(ns gilmour.hikari
  (:require
   [hikari-cp.core :as hikari]
   [com.stuartsierra.component :as c]))

(defrecord Hikari [config datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource config)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil)))

(defn make-hikari
  [config]
  (map->Hikari {:config config}))
