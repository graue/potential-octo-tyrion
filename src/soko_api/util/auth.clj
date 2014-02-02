(ns soko-api.util.auth
  (:require [soko-api.token :as token]
            [ring.util.codec :refer [base64-decode]]))

(defn parse-basic-auth
  "Parse an HTTP Basic authentication string, returning [user pass] or nil if
  invalid."
  [authstring]
  (some-> authstring
          (as-> s (re-find #"Basic (.+)" s))
          last
          base64-decode
          String.
          (as-> s (re-find #"([^:]*):(.*)" s))
          (subvec 1)))

(defn authenticate
  "Provide this function under the :allowed? key for any authenticated
  resource. Adds :token and :email keys to the request map if authentication
  succeeds, else returns nil."
  [ctx]
  (when-let [token (some-> ctx
                           (get-in [:request :headers "authorization"])
                           parse-basic-auth
                           first  ; Ignore pass; 'username' is token.
                           token/lookup)]
    (select-keys token [:token :email])))
