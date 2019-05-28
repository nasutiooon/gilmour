(ns gilmour.dev.repl
  (:require
   [clojure.tools.namespace.repl :refer [refresh disable-reload!]]
   [com.stuartsierra.component :as c]))

(disable-reload!)

(defonce ^:private initiator
  (atom nil))

(defonce ^:private system
  (atom nil))

(defn get-system
  []
  @system)

(defn set-init!
  [f]
  (reset! initiator f)
  ::ok)

(defn start!
  []
  (let [f @initiator]
    (if (nil? f)
      (throw (ex-info "No initiator to be found, have you called `gilmour.dev.repl/set-init!` ?" {}))
      (do (swap! system (comp c/start f))
          ::ok))))

(defn stop!
  []
  (swap! system #(when % (c/stop %)))
  ::ok)

(defn reset!
  []
  (stop!)
  (let [result (refresh :after gilmour.dev.repl/start!)]
    (if (instance? Throwable result)
      (throw result)
      result)))
