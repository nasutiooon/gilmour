(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [ring.util.http-response :refer [not-found]]))

(defrecord Router [routes resources handler]
  b/RouteProvider
  (routes [_] routes)

  c/Lifecycle
  (start [this]
    (let [handler (some-fn
                   (if resources
                     (make-handler ["" routes] resources)
                     (make-handler ["" routes]))
                   (constantly (not-found)))]
      (assoc this :handler handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn make-router
  ([routes]
   (map->Router {:routes routes}))
  ([routes resources]
   (map->Router {:routes routes :resources resources})))
