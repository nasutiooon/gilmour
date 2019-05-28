(ns gilmour.middleware)

(defprotocol SQLPool
  (pool [this]))

(defprotocol SQLSpec
  (db-spec [this]))
