(ns gilmour.sql)

(defprotocol SQLPool
  (pool [this])
  (pool-spec [this]))

(defprotocol SQLDb
  (db-spec [this]))
