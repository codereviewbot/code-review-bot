(ns com.ben-allred.code-review-bot.api.services.integrations.config
    (:require [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(def look-up
    (reify integrations/IIntegrator
        (process [_ payload]
            (let [config (-> payload
                             (get-in [:body :repository :html-url])
                             (configs/find-by-repo))]
                (cond-> payload
                    (seq config) (assoc :config config))))))
