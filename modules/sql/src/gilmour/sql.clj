(ns gilmour.sql)

(defprotocol SQLDb
  (db-spec [this]))
