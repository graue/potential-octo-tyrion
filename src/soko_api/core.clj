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

(defresource whoami-resource
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :malformed?
  (fn [ctx]
    (if-let [[token _]
             (-> ctx
                 (get-in [:request :headers "authorization"])
                 parse-basic-auth)]
      [false {:token token}]
      true))
  :allowed?
  (fn [ctx]
    (when-let [email (:email (token/lookup (:token ctx)))]
      {:email email}))
  :handle-ok #(response {:email (:email %)}))

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
