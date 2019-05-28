(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [gilmour.ring :as g.ring]
   [ring.util.http-response :refer [not-found]]))

(defprotocol ResourceProvider
  (resources [this]))

(defn- search-hooks
  [component]
  (->> (vals component)
       (filter (partial satisfies? b/RouteProvider))
       (filter (partial satisfies? ResourceProvider))))

(defrecord Router [request-routes request-resources not-found-handler handler]
  b/RouteProvider
  (routes [_] request-routes)

  c/Lifecycle
  (start [this]
    (let [hooks         (or (search-hooks this)
                            (throw (ex-info "Bidi router requires a hook" {})))
          req-routes    (->> hooks
                             (map b/routes)
                             (reduce merge {}))
          req-resources (->> hooks
                             (map resources)
                             (reduce merge {}))]
      (assoc this :request-routes req-routes :request-resources req-resources)))
  (stop [this]
    (assoc this :request-routes nil :request-resources nil))

  g.ring/RequestHandler
  (request-handler [_]
    (some-fn
     (make-handler ["" request-routes] request-resources)
     (or not-found-handler (constantly (not-found))))))

(defn make-router
  [config]
  (map->Router config))
