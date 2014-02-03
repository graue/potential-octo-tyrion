(ns soko-api.token
  (:require soko-api.config
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as str]
            [crypto.random :as rand])
  (:refer soko-api.config :rename {dbspec db}))

(defn lookup [token-str]
  (first (jdbc/query db ["SELECT email, token FROM tokens WHERE token = ?"
                         token-str])))

(defn- generate []
  (str/lower-case (rand/base32 15)))

(defn create! [email]
  (let [record {:email email
                :token (generate)}]
    (jdbc/insert! db :tokens record)
    record))

(defn invalidate! [token-str]
  (jdbc/delete! db :tokens ["token = ?" token-str]))
