(ns com.ben-allred.code-review-bot.services.middleware
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]
              [clojure.string :as string]))

(defn log-response [handler]
    (fn [request]
        (let [response (handler request)
              uri      (:uri request)]
            (when-not (or (= "/" uri) (re-find #"(^/js|^/css)" uri))
                (log/info (format "[%d] %s: %s"
                              (:status response)
                              (string/upper-case (name (:request-method request)))
                              uri)))
            response)))
