(ns gilmour.ring
  (:require
   [com.stuartsierra.component :as c]))

(defprotocol RequestHandler
  (request-handler [this]))

(defprotocol RequestMiddleware
  (request-middleware [this]))

(defprotocol ExHandler
  (ex-handlers [this]))

(defn- search-handler
  [component]
  (->> (vals component)
       (filter (partial satisfies? RequestHandler))
       (map request-handler)
       (first)))

(defrecord RingHead []
  RequestHandler
  (request-handler [this]
    (let [handler    (or (search-handler this)
                         (throw (ex-info "Ring head requires a handler" {})))
          middleware (->> (vals this)
                          (filter (partial satisfies? RequestMiddleware))
                          (map request-middleware)
                          (apply comp))]
      (fn [request]
        (let [new-handler (middleware handler)
              new-request (assoc request :component this)]
          (new-handler new-request))))))

(defn make-ring-head
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

(defn make-ring-middleware
  [config]
  (map->RingMiddleware config))

(defrecord ExceptionManager []
  RequestHandler
  (request-handler [this]
    (let [handler   (or (search-handler this)
                        (throw (ex-info "Ring head requires a handler" {})))
          catalogue (->> (vals this)
                         (filter (partial satisfies? ExHandler))
                         (map ex-handlers)
                         (reduce merge {}))]
      (fn [request]
        (try
          (handler request)
          (catch clojure.lang.ExceptionInfo e
            (let [-ex-data   (ex-data e)
                  kind       (:kind -ex-data)
                  ex-handler (get catalogue kind)]
              (if ex-handler
                (ex-handler request e -ex-data)
                (throw e)))))))))

(defn make-exception-manager
  []
  (map->ExceptionManager {}))
