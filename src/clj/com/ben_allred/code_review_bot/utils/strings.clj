(ns com.ben-allred.code-review-bot.utils.strings
    (:require [clojure.string :as string]))

(defn trim-to-nil [s]
    (let [s (string/trim s)]
        (if-not (empty? s)
            s
            nil)))
