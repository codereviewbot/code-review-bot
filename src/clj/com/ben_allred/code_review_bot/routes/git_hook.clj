(ns com.ben-allred.code-review-bot.routes.git-hook
    (:use compojure.core)
    (:require
        [com.ben-allred.code-review-bot.utils.logging :as log]))

(def webhooks
    (POST "/git-hook" req
        (log/warn (:body req))
        {:status 204}))
