(ns com.ben-allred.code-review-bot.api.services.integrations.rules
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]))

(defn ^:private rule->fn [payload]
    (fn [[path condition]]
        (when-let [value (get-in payload path)]
            (re-find (re-pattern condition) value))))

(defn ^:private message-key [rules payload]
    (first (for [[result conditions] rules
                 :when (every? (rule->fn payload) conditions)]
               result)))

(defn ^:private rand-message [messages key]
    (rand-nth (seq (get messages key))))

(def integrator
    (reify integrations/IIntegrator
        (process [_ payload]
            (let [{:keys [messages rules]} (:config payload)]
                (some->> payload
                    (:github)
                    (message-key rules)
                    (rand-message messages)
                    (assoc-in payload [:rules :message]))))))
