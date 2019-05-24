(ns gilmour.bidi
  (:require
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [com.stuartsierra.component :as c]
   [ring.util.http-response :refer [not-found]]))

(defrecord Router [routes routes-fn resources-fn not-found-handler handler]
  b/RouteProvider
  (routes [_] routes)

  c/Lifecycle
  (start [this]
    (let [routes    (routes-fn this)
          resources (when resources-fn (resources-fn this))
          handler   (some-fn
                     (if resources
                       (make-handler ["" routes] resources)
                       (make-handler ["" routes]))
                     (or not-found-handler (constantly (not-found))))]
      (assoc this :routes routes :handler handler)))
  (stop [this]
    (assoc this :routes nil :handler nil)))

(defn make-router
  [opts]
  (map->Router opts))
