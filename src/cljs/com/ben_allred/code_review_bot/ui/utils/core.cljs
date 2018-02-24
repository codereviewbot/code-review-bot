(ns com.ben-allred.code-review-bot.ui.utils.core
    (:require [com.ben-allred.code-review-bot.utils.strings :as strings]))

(defn classes
    ([rules] (classes nil rules))
    ([attrs rules]
     (let [attrs' (reduce #(cond-> %1
                               (val %2) (update :class str " " (name (key %2))))
                          attrs
                          rules)]
         (update attrs' :class strings/trim-to-nil))))
