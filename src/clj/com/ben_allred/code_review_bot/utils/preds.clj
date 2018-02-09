(ns com.ben-allred.code-review-bot.utils.preds
    (:import [java.util.regex Pattern]))

(def regex? (partial instance? Pattern))
