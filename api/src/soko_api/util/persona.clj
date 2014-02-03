(ns soko-api.util.persona
  (:require [clj-http.client :as client]))

(defn verify
  "Returns raw assertion response using Mozilla's Remote Verification API.
  Note that the return value is *always* truthy — it's a map. You must call
  valid? on the returned map to ensure verification succeeded, and check the
  returned map's :email key to be sure it matches the expected email.
  
  `audience` is a string containing the protocol, domain name and port of
  your site. Example: \"http://example.com:80\". This must be hardcoded in
  the server configuration, not come from a client-side request.
  
  This function blocks on an outgoing HTTP request to the API."
  [assertion audience]
  (if-let [http-response
           (client/post "https://verifier.login.persona.org/verify"
                        {:as :json
                         :form-params {:assertion assertion
                                       :audience audience}
                         :throw-exceptions false
                         :ignore-unknown-host? true})]
    (if (= (:status http-response) 200)
      (:body http-response)
      {:status "failure"
       :reason (str "Verification server returned unexpected code: "
                    (:status http-response))})
    {:status "failure"
     :reason "Could not connect to Persona verification server"}))

(defn valid?
  "Checks if the assertion response is valid. Again, make sure to also check
  that the response's :email field is as expected!"
  [assertion-response]
  (= (:status assertion-response) "okay"))
