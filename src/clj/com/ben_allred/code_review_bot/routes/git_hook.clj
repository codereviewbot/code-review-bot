(ns com.ben-allred.code-review-bot.routes.git-hook
    (:use compojure.core)
    (:require
        [com.ben-allred.code-review-bot.utils.logging :as log]))

(def webhooks
    (POST "/git-hook" req
        (log/warn "headers: " (:headers req))
        (log/warn "commiter: " (get-in req [:body :head_commit :committer]))
        (log/warn "message: " (get-in req [:body :head_commit :message]))
        (log/warn "repository: " (get-in req [:body :repository :html_url]))
        (log/warn "ref: " (get-in req [:body :ref]))
        {:status 204}))
