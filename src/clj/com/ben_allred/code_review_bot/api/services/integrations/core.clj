(ns com.ben-allred.code-review-bot.api.services.integrations.core)

(defprotocol IIntegrator
    (process [this payload]))

(extend-protocol IIntegrator
    nil
    (process [_ payload]
        payload))
