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
