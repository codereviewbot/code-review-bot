(ns com.ben-allred.code-review-bot.utils.preds
    (:import [java.util.regex Pattern]))

(defn or? [& preds]
    (fn [value]
        (reduce #(or %1 (%2 value)) false preds)))

(def regexp?
    (partial instance? Pattern))
