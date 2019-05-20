(defproject gilmour/datomic "0.1.4"
  :description "Datomic as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/datomic"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[com.datomic/datomic-free "0.9.5697"]
                 [io.rkn/conformity "0.5.1"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0-beta3"]
                                  [com.stuartsierra/component "0.4.0"]]}})
