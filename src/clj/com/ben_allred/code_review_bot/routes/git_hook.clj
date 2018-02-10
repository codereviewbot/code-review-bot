(ns com.ben-allred.code-review-bot.routes.git-hook
    (:use compojure.core)
    (:require
        [com.ben-allred.code-review-bot.utils.logging :as log]
        [com.ben-allred.code-review-bot.services.rules :as rules]
        [clojure.string :as string]
        [clojure.set :as set]
        [com.ben-allred.code-review-bot.services.slack :as slack]
        [com.ben-allred.code-review-bot.services.mongo :as mongo]))

(defn ^:private ref->branch [ref]
    (-> ref
        (string/split #"\/")
        (last)))

(defn ^:private wrap-message [payload message]
    (str "I have reviewed your commit:\n> " message "\n" (get-in payload [:commit :url])))

(def webhooks
    (POST "/git-hook" req
        (let [config (mongo/find-one {:repo-url (get-in req [:body :repository :html_url])})
              {commit :head_commit :as body} (:body req)
              payload {:commit     commit
                       :repository (:repository body)
                       :branch     (ref->branch (:ref body))}]
            (if config
                (some->> payload
                    (rules/message-key (:rules config))
                    (rules/rand-message (:messages config))
                    (wrap-message payload)
                    (slack/send-hook (:slack-path config)))
                (log/warn "Could not find integration for:" payload)))
        {:status 204}))
