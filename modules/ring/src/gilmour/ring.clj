(ns gilmour.ring
  (:require
   [com.stuartsierra.component :as c]))

(defprotocol RequestHandler
  (request-handler [this]))

(defprotocol RequestMiddleware
  (request-middleware [this]))

(defrecord RingHead []
  RequestHandler
  (request-handler [this]
    (let [handler    (->> (vals this)
                          (filter (partial satisfies? RequestHandler))
                          (map request-handler)
                          (first))
          middleware (->> (vals this)
                          (filter (partial satisfies? RequestMiddleware))
                          (map request-middleware)
                          (apply comp))]
      (fn [request]
        (let [new-handler (middleware handler)
              new-request (assoc request :component this)]
          (new-handler new-request))))))

(defn make-ring-head
  []
  (map->RingHead {}))

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

(defrecord RingMiddleware [entries wrapper]
  RequestMiddleware
  (request-middleware [this]
    (compose this entries)))

(defn make-ring-middleware
  [config]
  (map->RingMiddleware config))
