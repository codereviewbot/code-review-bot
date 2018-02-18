(ns com.ben-allred.code-review-bot.api.services.integrations.rules
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]))

(defn ^:private rule->fn [payload]
    (fn [[path condition]]
        (when-let [value (get-in payload path)]
            (re-find (re-pattern condition) value))))

(defn ^:private message-key [rules payload]
    (->> rules
        (filter (comp (partial every? (rule->fn payload)) second))
        (ffirst)))

(defn ^:private rand-message [messages key]
    (rand-nth (seq (get messages key))))

(def match-rule
    (reify integrations/IIntegrator
        (process [_ payload]
            (let [{:keys [messages rules]} (:config payload)
                  message
                  (some->> payload
                      (:github)
                      (message-key rules)
                      (rand-message messages))]
                (cond-> payload
                    message (assoc-in [:rules :message] message))))))
