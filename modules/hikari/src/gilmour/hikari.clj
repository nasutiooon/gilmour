(ns gilmour.hikari
  (:require
   [hikari-cp.core :as hikari]
   [com.stuartsierra.component :as c]))

(defrecord Hikari [db-spec datasource]
  c/Lifecycle
  (start [this]
    (assoc this :datasource (hikari/make-datasource db-spec)))
  (stop [this]
    (when datasource (hikari/close-datasource datasource))
    (assoc this :datasource nil)))

(defn make-hikari
  [db-spec]
  (map->Hikari {:db-spec db-spec}))
