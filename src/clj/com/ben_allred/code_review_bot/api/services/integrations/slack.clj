(ns com.ben-allred.code-review-bot.api.services.integrations.slack
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.services.http :as http]))

(def ^:private slack-url "https://hooks.slack.com")

(defn ^:private wrap-message [commit-url commit-message rule-message]
    (when (and commit-message commit-url rule-message)
        (str "I have reviewed the code in your commit:\n"
            "\n<" commit-url "|" commit-message ">"
            "\n> " rule-message)))

(defn ^:private post-to-slack [{:keys [github rules config] :as payload}]
    (let [{:keys [url message]} (:commit github)
          slack-message (wrap-message url message (:message rules))
          slack-path    (:slack-path config)]
        (when (and slack-message slack-path)
            (http/post (str slack-url slack-path) {:body {:text slack-message}})
            payload)))

(def integrator
    (reify integrations/IIntegrator
        (process [_ payload]
            (post-to-slack payload))))
