(ns gilmour.ring
  (:require
   [com.stuartsierra.component :as c]))

(defprotocol RequestHandler
  (request-handler [this]))

(extend-protocol RequestHandler
  clojure.lang.AFunction
  (request-handler [this] this))

(defprotocol RequestMiddleware
  (request-middleware [this]))

(extend-protocol RequestMiddleware
  clojure.lang.AFunction
  (request-handler [this] this))

(defprotocol ExHandler
  (ex-handlers [this]))

(defn- search-handler
  [component]
  (or (when-let [handler (:handler component)]
        (request-handler handler))
      (->> (vals component)
           (filter (partial satisfies? RequestHandler))
           (map request-handler)
           (first))))

(defrecord RingHead []
  RequestHandler
  (request-handler [this]
    (let [handler    (or (search-handler this)
                         (throw (ex-info "ring head requires a handler" {})))
          middleware (->> (vals this)
                          (filter (partial satisfies? RequestMiddleware))
                          (map request-middleware)
                          (apply comp))]
      (middleware handler))))

(defn ring-head
  []
  (map->RingHead {}))

(defn- substitute
  [component entry]
  (if (vector? entry)
    (replace {:component component} entry)
    entry))

(defn- coerce
  [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

(defn- compose
  [component entries]
  (->> entries
       (map (partial substitute component))
       (map coerce)
       (apply comp)))

(defrecord RingMiddleware [entries wrapper]
  RequestMiddleware
  (request-middleware [this]
    (compose this entries)))

(defn ring-middleware
  [config]
  (map->RingMiddleware config))

(defrecord RingExManager []
  RequestHandler
  (request-handler [this]
    (let [handler   (or
                     (search-handler this)
                     (throw (ex-info "ring ex manager requires a handler" {})))
          catalogue (->> (vals this)
                         (filter (partial satisfies? ExHandler))
                         (map ex-handlers)
                         (reduce merge {}))]
      (fn [request]
        (try
          (handler request)
          (catch Exception e
            (let [kind       (if (instance? clojure.lang.ExceptionInfo e)
                               (-> e ex-data :kind)
                               (type e))
                  ex-handler (get catalogue kind)]
              (if ex-handler
                (ex-handler e request)
                (throw e)))))))))

(defn ring-ex-manager
  []
  (map->RingExManager {}))
