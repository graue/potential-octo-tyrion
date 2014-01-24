(ns soko-api.token
  (:require soko-api.config
            [clojure.java.jdbc :refer [query insert!]]
            [clojure.string :as str]
            [crypto.random :as rand])
  (:refer soko-api.config :rename {dbspec db}))

(defn lookup [token-str]
  (first (query db ["SELECT email, token FROM tokens WHERE token = ?"
                    token-str])))

(defn- generate []
  (str/lower-case (rand/base32 15)))

(defn create! [email]
  (let [record {:email email
                :token (generate)}]
    (insert! db :tokens record)
    record))
