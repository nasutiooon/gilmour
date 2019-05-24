(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [ring.util.http-response :refer [not-found]]))

(defprotocol ResourceProvider
  (resources [this]))

(defn- search-resource-hooks
  [component]
  (or (->> (vals component)
           (filter #(and (satisfies? b/RouteProvider %)
                         (satisfies? ResourceProvider %)))
           (not-empty))
      (throw (ex-info "Can't find any attached resource" {}))))

(defrecord Router [routes not-found-handler handler]
  b/RouteProvider
  (routes [_] routes)

  c/Lifecycle
  (start [this]
    (let [hooks     (search-resource-hooks this)
          routes    (->> hooks
                         (map b/routes)
                         (reduce merge {}))
          resources (->> hooks
                         (map resources)
                         (reduce merge {}))
          handler   (some-fn
                     (make-handler ["" routes] resources)
                     (or not-found-handler (constantly (not-found))))]
      (assoc this :routes routes :handler handler)))
  (stop [this]
    (assoc this :routes nil :handler nil)))

(defn make-router
  [opts]
  (map->Router opts))
