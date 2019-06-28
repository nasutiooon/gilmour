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

(defn search-middleware
  [component]
  (->> (vals component)
       (filter (partial satisfies? g.ring/RequestMiddleware))
       (map g.ring/request-middleware)
       (apply comp)))

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
                             (map (juxt search-middleware resources))
                             (map (fn [[mdw rscs]]
                                    (reduce-kv (fn [m k rsc]
                                                 (assoc m k (mdw rsc)))
                                               {}
                                               rscs)))
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

(defrecord ResourceHooks [routes resources]
  b/RouteProvider
  (routes [_] routes)

  ResourceProvider
  (resources [_] resources))

(defn make-resource-hooks
  [config]
  (map->ResourceHooks config))

(defn path-for
  ([router handler route-params]
   (let [routes ["" (b/routes router)]]
     (apply b/path-for routes handler (flatten (seq route-params)))))
  ([router handler]
   (path-for router handler {})))

(defn match-route
  ([router path options]
   (let [routes ["" (b/routes router)]]
     (apply b/match-route routes path (flatten (seq options)))))
  ([router path]
   (match-route router path {})))
