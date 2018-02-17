(ns com.ben-allred.code-review-bot.api.services.middleware
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.api.services.jwt :as jwt]
              [com.ben-allred.code-review-bot.services.content :as content]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn log-response [handler]
    (fn [request]
        (let [response (handler request)
              uri      (:uri request)]
            (when-not (or (= "/" uri) (re-find #"(^/js|^/css)" uri))
                (log/info (format "[%d] %s: %s"
                              (or (:status response) 404)
                              (string/upper-case (name (:request-method request)))
                              uri)))
            response)))

(defn decode-jwt [handler]
    (fn [request]
        (-> request
            (assoc :user (:data (jwt/decode (get-in request [:cookies "auth-token" :value]))))
            (handler))))

(defn content-type [handler]
    (fn [request]
        (let [headers (:headers request)]
            (-> request
                (content/parse (get headers "content-type"))
                (handler)
                (content/prepare (get headers "accept"))))))
