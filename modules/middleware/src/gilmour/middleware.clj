(ns gilmour.middleware
  (:require
   [com.stuartsierra.component :as c]))

(defn- substitute
  [component entry]
  (if (vector? entry)
    (replace {:component component} entry)
    entry))

(defn- coerce
  [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

(defn- compose
  [component entries]
  (->> entries
       (map (comp coerce (partial substitute component)))
       (apply comp)))

(defrecord Middleware [entries wrapper]
  c/Lifecycle
  (start [this]
    (assoc this :wrapper (compose this entries)))
  (stop [this]
    (assoc this :wrapper nil)))

(defn make-middleware
  [config]
  (map->Middleware config))
