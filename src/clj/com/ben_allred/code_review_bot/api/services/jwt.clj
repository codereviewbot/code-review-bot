(ns com.ben-allred.code-review-bot.api.services.jwt
    (:require [clj-jwt.core :as jwt]
              [clj-time.core :as time]
              [com.ben-allred.code-review-bot.services.env :as env]
              [com.ben-allred.code-review-bot.utils.json :as json])
    (:import [java.util Date]))

(def ^:private jwt-secret (env/get :jwt-secret))

(defn valid? [token]
    (try
        (jwt/verify (jwt/str->jwt token) jwt-secret)
        (catch Throwable e
            false)))

(defn decode [jwt]
    (when (valid? jwt)
        (:claims (jwt/str->jwt jwt))))

(defn encode
    ([payload] (encode payload 30))
    ([payload days-to-expire]
     (let [now (time/now)]
         (-> {:data payload :iat now}
             (assoc :exp (time/plus now (time/days days-to-expire)))
             (jwt/jwt)
             (jwt/sign :HS256 jwt-secret)
             (jwt/to-str)))))
