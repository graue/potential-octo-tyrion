(defproject soko-api "0.0.1-SNAPSHOT"
  :description "Backend API for Sojoban"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [liberator "0.10.0"]
                 [compojure "1.1.6"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring/ring-codec "1.0.0"]
                 [ring/ring-json "0.2.0"]
                 [clj-http "0.7.8"]
                 [org.postgresql/postgresql "9.3-1100-jdbc41"]
                 [org.clojure/java.jdbc "0.3.0-beta1"]
                 [lobos "1.0.0-beta1"]
                 [environ "0.4.0"]
                 [crypto-random "1.1.0"]]
  :plugins [[lein-ring "0.8.8"]
            [lein-environ "0.4.0"]]
  :ring {:handler soko-api.core/handler}
  :profiles
  {:dev {:dependencies []
         :env {:soko-db-user "dbuser"
               :soko-db-table "soko"}}})
