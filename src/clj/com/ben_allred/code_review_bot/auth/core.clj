(ns com.ben-allred.code-review-bot.auth.core
    (:use [compojure.core])
    (:require [ring.util.response :as resp]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.utils.env :as env]
              [com.ben-allred.code-review-bot.services.jwt :as jwt]))

(defn ^:private token->cookie [resp value]
    (->> value
        (jwt/encode)
        (assoc {:path "/" :http-only true} :value)
        (assoc-in resp [:cookies "auth-token"])))

(defroutes auth
    (GET "/login" []
        (-> (str (env/env :base-url) "/")
            (resp/redirect)
            (token->cookie {:email "some@email.here"})))
    (GET "/logout" req
        (-> (str (env/env :base-url) "/")
            (resp/redirect)
            (assoc :cookies {"auth-token" {:value "" :path "/" :http-only true :max-age 0}})))
    (GET "/details" req
        (let [details (jwt/decode (get-in req [:cookies "auth-token" :value]))]
            (if (seq details)
                {:status 200 :body details}
                {:status 401}))))


