(defproject gilmour/jwt-encoder "0.1.2"
  :description "Jwt encoder as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/jwt_encoder"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[buddy/buddy-core "1.5.0"]
                 [buddy/buddy-sign "3.0.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0-beta3"]
                                  [com.stuartsierra/component "0.4.0"]]}})
