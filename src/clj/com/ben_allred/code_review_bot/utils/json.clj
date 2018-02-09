(ns com.ben-allred.code-review-bot.utils.json
    (:require [jsonista.core :as jsonista]))

(defn parse [s]
    (jsonista/read-value s))

(defn stringify [o]
    (jsonista/write-value-as-string o))
