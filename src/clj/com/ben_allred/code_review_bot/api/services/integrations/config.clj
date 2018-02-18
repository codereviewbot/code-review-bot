(ns com.ben-allred.code-review-bot.api.services.integrations.config
    (:require [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]))

(def integrator
    (reify integrations/IIntegrator
        (process [_ payload]
            (let [config (-> payload
                             (get-in [:body :repository :url])
                             (configs/find-by-repo))]
                (when (seq config)
                    (assoc payload :config config))))))
