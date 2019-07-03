(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [gilmour.ring :as g.ring]
   [ring.util.http-response :as res]))

(defprotocol ResourceProvider
  (resources [this]))

(defn- search-hooks
  [component]
  (or (->> (vals component)
           (filter (partial satisfies? b/RouteProvider))
           (filter (partial satisfies? ResourceProvider)))
      (throw (ex-info "bidi router requires a hook" {}))))

(defrecord Router [request-routes request-resources not-found-handler handler]
  b/RouteProvider
  (routes [_] request-routes)

  c/Lifecycle
  (start [this]
    (let [hooks         (search-hooks this)
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

(defn router
  [config]
  (map->Router config))

(defrecord ResourcesHook [routes resources]
  b/RouteProvider
  (routes [_] routes)

  ResourceProvider
  (resources [_] resources))

(defn resources-hook
  [config]
  (map->ResourcesHook config))

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
