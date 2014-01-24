(ns soko-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response
                                          wrap-json-params]]
            [ring.util.codec :refer [base64-decode]]
            [clojure.string :as str]
            [soko-api.token :as token]))

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
  resource. Adds a :token key to the request map if authentication is
  successful (the value contains :token and :email keys), else returns nil."
  [ctx]
  (when-let [token (some-> ctx
                           (get-in [:request :headers "authorization"])
                           parse-basic-auth
                           token/lookup)]
    {:token token}))

(defresource whoami-resource
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :allowed? authenticate
  :handle-ok #(response {:email (get-in % [:token :email])}))

(defresource token-list-resource
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  )

(defroutes app
  (ANY "/whoami" [] whoami-resource)
  #_(ANY "/token" [] token-list-resource)
  #_(ANY "/token/:id" [id] (token-resource id)))

(def handler
  (-> app
      (wrap-params)
      (wrap-json-response)
      (wrap-json-params)))
