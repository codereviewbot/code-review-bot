(ns com.ben-allred.code-review-bot.services.slack
    (:require [com.ben-allred.code-review-bot.utils.http :as http]))

(def url "https://hooks.slack.com")

(def path "/services/T0A14MGAE/B973L3J9K/UA05yRTCivUZO39hT0jm9Ibn")

(defn send-hook [path message]
    (http/post (str url path) {:headers {:Content-type "application/json"}
                               :body    {:text message}}))
