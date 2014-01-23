(ns soko-api.token
  (:require soko-api.config
            [clojure.java.jdbc :refer [query]])
  (:refer soko-api.config :rename {dbspec db}))

(defn lookup [token-str]
  (first (query db ["SELECT email, token FROM tokens WHERE token = ?"
                    token-str])))
