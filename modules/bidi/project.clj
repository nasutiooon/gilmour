(defproject gilmour/bidi "0.1.8"
  :description "Bidi as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/bidi"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[bidi "2.1.6"]
                 [metosin/ring-http-response "0.9.1"]]
  :repositories [["releases" {:url   "https://repo.clojars.org"
                              :creds :gpg}]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0-beta3"]
                                  [com.stuartsierra/component "0.4.0"]]}})
