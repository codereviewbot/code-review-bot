(ns com.ben-allred.code-review-bot.api.hooks
    (:use compojure.core)
    (:require
        [com.ben-allred.code-review-bot.utils.logging :as log]
        [com.ben-allred.code-review-bot.services.rules :as rules]
        [clojure.string :as string]
        [clojure.set :as set]
        [com.ben-allred.code-review-bot.services.slack :as slack]
        [com.ben-allred.code-review-bot.db.models.configs :as configs]
        [com.ben-allred.code-review-bot.utils.strings :as strings]))

(defn ^:private ref->branch [ref]
    (-> ref
        (str)
        (string/split #"\/")
        (last)
        (strings/trim-to-nil)))

(defn ^:private req->payload [{:keys [body]}]
    {:branch (ref->branch (:ref body))
     :commit (:head_commit body)
     :repository (:repository body)})

(defn ^:private wrap-message [commit message]
    (str "I have reviewed the code in your commit:\n" (:url commit) "\n> " message))

(defn ^:private handle-integration [config {:keys [commit] :as payload}]
    (some->> payload
        (rules/message-key (:rules config))
        (rules/rand-message (:messages config))
        (wrap-message commit)
        (slack/send-hook (:slack-path config))))

(def webhooks
    (POST "/hooks/git" req
        (let [repo-url (get-in req [:body :repository :html_url])
              config (configs/find-by-repo repo-url)
              payload (req->payload req)]
            (log/info "received payload:" payload)
            (if config
                (do (handle-integration config payload)
                    {:status 204})
                {:status 404 :body {:message "Integration not found"}}))))
