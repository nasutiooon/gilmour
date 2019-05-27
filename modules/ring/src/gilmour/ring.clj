(ns gilmour.ring
  (:require
   [com.stuartsierra.component :as c]))

(defprotocol RequestHandler
  (request-handler [this]))

(defprotocol RequestMiddleware
  (request-middleware [this]))

(defrecord RingHandler [middleware-kws]
  RequestHandler
  (request-handler [this]
    (let [handler    (or (->> (vals this)
                              (filter (partial satisfies? RequestHandler))
                              (map request-handler)
                              (first))
                         (throw
                          (ex-info "Ring handler requires a request handler"
                                   {})))
          middleware (let [ms (if (seq middleware-kws)
                                (reduce (fn [c kw]
                                          (if-let [v (get this kw)]
                                            (conj c v)
                                            c))
                                        []
                                        middleware-kws)
                                (vals this))]
                       (->> ms
                            (filter (partial satisfies? RequestMiddleware))
                            (map request-middleware)
                            (apply comp)))]
      (middleware handler))))

(defn make-ring-handler
  [config]
  (map->RingHandler config))

(defrecord RingMiddleware [wrapper]
  RequestMiddleware
  (request-middleware [this] (wrapper this)))

(defn make-ring-middleware
  [config]
  (map->RingMiddleware config))
