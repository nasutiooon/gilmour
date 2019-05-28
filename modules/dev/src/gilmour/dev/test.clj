(ns gilmour.dev.test
  (:require
   [com.stuartsierra.component :as c]))

(defonce ^:private system
  (atom nil))

(defn get-system
  []
  @system)

(defn with-system
  [system-f]
  (fn [f]
    (reset! system (c/start (system-f)))
    (f)
    (swap! system c/stop)))
