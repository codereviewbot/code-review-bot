(ns com.ben-allred.code-review-bot.api.utils.response
    (:require [com.ben-allred.code-review-bot.services.http :as http]))

(defn respond [[status body headers]]
    (cond-> {:status 200}
        status (assoc :status (http/kw->status status status))
        body (assoc :body body)
        headers (assoc :headers headers)))
