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

(defrecord Middleware [config entries middleware]
  c/Lifecycle
  (start [this]
    (assoc this :middleware (compose this entries)))
  (stop [this]
    (assoc this :middleware nil)))

(defn make-middleware
  [config opts]
  (-> opts
      (assoc :config config)
      (map->Middleware)))
