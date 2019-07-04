(defproject gilmour/bidi "0.1.18"
  :description "Bidi as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/bidi"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[bidi "2.1.6"]
                 [metosin/ring-http-response "0.9.1"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [com.stuartsierra/component]
                                  [gilmour/ring]]}})
