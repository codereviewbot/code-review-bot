(ns com.ben-allred.code-review-bot.api.services.slack
    (:require [com.ben-allred.code-review-bot.services.http :as http]))

(def url "https://hooks.slack.com")

(defn send-hook [path message]
    (http/post (str url path) {:headers {:Content-type "application/json"}
                               :body    {:text message}}))
