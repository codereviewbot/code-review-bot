(ns com.ben-allred.code-review-bot.routes.git-hook
    (:use compojure.core)
    (:require
        [com.ben-allred.code-review-bot.utils.logging :as log]
        [com.ben-allred.code-review-bot.services.rules :as rules]
        [clojure.string :as string]
        [clojure.set :as set]
        [com.ben-allred.code-review-bot.services.slack :as slack]))

(defn ^:private ref->branch [ref]
    (-> ref
        (string/split #"\/")
        (last)))

(defn ^:private wrap-message [payload message]
    (str "I have reviewed your commit:\n> " message "\n" (get-in payload [:commit :url])))

(def webhooks
    (POST "/git-hook" req
        (let [{commit :head_commit :as body} (:body req)
              payload {:commit     commit
                       :repository (:repository body)
                       :branch     (ref->branch (:ref body))}]
            (some->> payload
                (rules/message-key rules/rules)
                (rules/rand-message rules/messages)
                (wrap-message payload)
                (slack/send-hook slack/path)))
        {:status 204}))
