(defproject gilmour/jwt-encoder "0.1.4"
  :description "Jwt encoder as component"
  :url "https://github.com/nasutiooon/gilmour/tree/master/modules/jwt_encoder"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[buddy/buddy-core "1.5.0"]
                 [buddy/buddy-sign "3.0.0"]]
  :plugins [[lein-parent "0.3.2"]]
  :parent-project {:path    "../../project.clj"
                   :inherit [:deploy-repositories :managed-dependencies]}
  :profiles {:dev {:dependencies [[org.clojure/clojure]
                                  [com.stuartsierra/component]]}})
