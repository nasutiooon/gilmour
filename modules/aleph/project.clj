(defproject gilmour/aleph "0.1.5"
  :description "Aleph as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/aleph"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aleph "0.4.7-alpha5"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0-beta3"]
                                  [com.stuartsierra/component "0.4.0"]]}})
