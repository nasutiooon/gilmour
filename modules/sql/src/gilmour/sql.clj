(ns gilmour.sql)

(defprotocol SQLPool
  (pool [this]))

(defprotocol SQLSpec
  (db-spec [this]))
