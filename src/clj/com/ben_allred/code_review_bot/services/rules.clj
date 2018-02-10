(ns com.ben-allred.code-review-bot.services.rules
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private rule->fn [payload]
    (fn [[path condition]]
        (re-find (re-pattern (str condition)) (get-in payload path ""))))

(defn message-key [rules payload]
    (first (for [[result conditions] rules
                 :when (every? (rule->fn payload) conditions)]
               result)))

(defn rand-message [messages key]
    (rand-nth (seq (get messages key))))
