(defproject gilmour/postgres "0.1.6"
  :description "Postgres as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/postgres"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.postgresql/postgresql "42.2.6"]
                 [funcool/clojure.jdbc "0.9.0"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [gilmour/sql]]}})
