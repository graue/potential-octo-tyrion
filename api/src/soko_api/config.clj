(ns soko-api.config
  (:require [environ.core :refer [env]]))

(def dbspec
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :user (get env :soko-db-user "dbuser")
   :password (get env :soko-db-pass "")
   :subname
   (str "//" (get env :soko-db-host "localhost")
        ":" (get env :soko-db-port 5432)
        "/" (get env :soko-db-name "soko"))})

(def base-url (get env :soko-base-url))
