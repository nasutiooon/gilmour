(defproject gilmour/migratus "0.1.1"
  :description "Migratus as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/migratus"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[migratus "1.2.3"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [gilmour/sql]]}})
