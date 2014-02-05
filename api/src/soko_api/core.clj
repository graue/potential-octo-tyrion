(ns soko-api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-params]]
            [clojure.string :as str]
            [soko-api.token :as token]
            [soko-api.util.persona :as persona]
            [soko-api.util.auth :refer [authenticate]]
            [soko-api.config :refer [base-url]]))

(defresource whoami-resource
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :allowed? authenticate
  :handle-ok #(select-keys % [:email]))

(defresource token-list-resource
  :allowed-methods [:post]
  :available-media-types ["application/json"]
  :malformed?
  (fn [ctx]
    (if-let [assertion (get-in ctx [:request :params "assertion"])]
      [false {:assertion assertion}]
      [true {:representation {:media-type "application/json"}}]))
  :handle-malformed (fn [_] {:error "Request must include an assertion"})
  :allowed?
  (fn [ctx]
    (let [assertion-response (persona/verify (:assertion ctx) base-url)]
      (if (persona/valid? assertion-response)
        [true {:assertion-response assertion-response}]
        [false {:error (str "Assertion could not be verified. "
                            (:reason assertion-response))
                :representation {:media-type "application/json"}}])))
  :handle-forbidden (fn [ctx] {:error (:error ctx)})
  :post!
  (fn [ctx]
    (let [token (token/create! (get-in ctx [:assertion-response :email]))]
      {:record token
       :location (str base-url "/token/" (:token token))}))
  :handle-created (fn [ctx] (:record ctx)))

(defresource token-resource [id]
  :allowed-methods [:delete]
  :media-type-available? true  ; Never returns content, so...
  :exists? (fn [_] (token/lookup id))
  :delete! (fn [_] (token/invalidate! id))
  :respond-with-entity? false)

(defroutes app
  (ANY "/whoami" [] whoami-resource)
  (ANY "/token" [] token-list-resource)
  (ANY "/token/:id" [id] (token-resource id))
  (route/files "/" {:root "../frontend/public"}))

(def handler
  (-> app
      (wrap-params)
      (wrap-json-params)))
