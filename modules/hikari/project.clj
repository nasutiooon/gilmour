(defproject gilmour/hikari "0.1.11"
  :description "Hikari as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/hikari"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[hikari-cp "2.8.0"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [com.stuartsierra/component]
                                  [gilmour/sql]]}})
