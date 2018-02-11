(ns com.ben-allred.code-review-bot.utils.keywords)

(defn safe-name [v]
    (if (keyword? v)
        (name v)
        v))
