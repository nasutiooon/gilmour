(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [ring.util.http-response :refer [not-found]]))

(defrecord Router [routes resources not-found-handler handler]
  b/RouteProvider
  (routes [_] routes)

  c/Lifecycle
  (start [this]
    (let [routes  (if (fn? routes) (routes this) routes)
          handler (some-fn
                   (if resources
                     (make-handler ["" routes] (if (fn? resources)
                                                 (resources this)
                                                 resources))
                     (make-handler ["" routes]))
                   (or not-found-handler (constantly (not-found))))]
      (assoc this :handler handler)))
  (stop [this]
    (assoc this :handler nil)))

(defn make-router
  [opts]
  (map->Router opts))
