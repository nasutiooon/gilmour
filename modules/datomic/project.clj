(defproject gilmour/datomic "0.1.10"
  :description "Datomic as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/datomic"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[com.datomic/datomic-free "0.9.5697"]
                 [io.rkn/conformity "0.5.1"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [com.stuartsierra/component]]}})
