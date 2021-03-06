(defproject gilmour "0.1.8"
  :description "Collection of component"
  :url "https://github.com/nasutiooon/gilmour"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :managed-dependencies [[org.clojure/clojure "1.10.0-beta3"]
                         [com.stuartsierra/component "0.4.0"]
                         [gilmour/ring "0.1.9"]
                         [gilmour/sql "0.1.4"]]
  :plugins [[lein-sub "0.3.0"]]
  :sub ["modules/aleph"
        "modules/bidi"
        "modules/dev"
        "modules/handler"
        "modules/middleware"
        "modules/jwt_encoder"
        "modules/datomic"
        "modules/hikari"
        "modules/ragtime"
        "modules/ring"
        "modules/sql"
        "modules/postgres"
        "modules/migratus"]
  :deploy-repositories [["clojars" {:url      "https://clojars.org/repo"
                                    :username :env/clojars_username
                                    :password :env/clojars_password}]]
  :aliases {"deploy-all" ["sub" "deploy" "clojars"]})
