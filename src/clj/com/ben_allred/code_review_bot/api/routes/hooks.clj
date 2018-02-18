(ns com.ben-allred.code-review-bot.api.routes.hooks
    (:use compojure.core)
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.api.services.integrations.rules :as rules]
              [com.ben-allred.code-review-bot.api.services.integrations.slack :as slack]
              [com.ben-allred.code-review-bot.api.services.integrations.github :as github]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.api.services.integrations.config :as config]
              [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]))

(defn ^:private handle-integration [payload]
    (some->> payload
        (integrations/process config/integrator)
        (integrations/process github/integrator)
        (integrations/process rules/integrator)
        (integrations/process slack/integrator)))

(def webhooks
    (POST "/git" req
        (if (handle-integration (select-keys req [:body]))
            {:status 204}
            {:status 404 :body {:message "Integration not found"}})))
