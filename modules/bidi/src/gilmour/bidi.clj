(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [ring.util.http-response :refer [not-found]]))

(defrecord Router [routes resources not-found-fn handler]
  b/RouteProvider
  (routes [_] routes)

  c/Lifecycle
  (start [this]
    (let [handler (some-fn
                   (if resources
                     (make-handler ["" routes] resources)
                     (make-handler ["" routes]))
                   (or not-found-fn (constantly (not-found))))]
      (assoc this :handler handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn make-router
  ([routes]
   (map->Router {:routes routes}))
  ([routes {:keys [resources not-found-fn]}]
   (map->Router {:routes       routes
                 :resources    resources
                 :not-found-fn not-found-fn})))
