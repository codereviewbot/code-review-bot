(ns com.ben-allred.code-review-bot.api.routes.auth
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.api.services.auth :as auth]
              [com.ben-allred.code-review-bot.api.services.jwt :as jwt]))

(defroutes auth
    (GET "/login" []
        (auth/login))
    (GET "/logout" []
        (auth/logout))
    (GET "/details" req
        (let [details (jwt/decode (get-in req [:cookies "auth-token" :value]))]
            (if (seq details)
                {:status 200 :body details}
                {:status 401})))
    (GET "/callback" req
        (auth/callback (:query-params req))))
