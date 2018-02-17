(ns com.ben-allred.code-review-bot.api.services.rules
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private rule->fn [payload]
    (fn [[path condition]]
        (when-let [value (get-in payload path)]
            (re-find (re-pattern condition) value))))

(defn message-key [rules payload]
    (first (for [[result conditions] rules
                 :when (every? (rule->fn payload) conditions)]
               result)))

(defn rand-message [messages key]
    (rand-nth (seq (get messages key))))
