(ns com.ben-allred.code-review-bot.utils.logging
    (:require [clojure.tools.logging :as logger]))

(defmacro debug [& args]
    `(logger/debug ~@args))

(defmacro info [& args]
    `(logger/info ~@args))

(defmacro warn [& args]
    `(logger/warn ~@args))

(defmacro error [& args]
    `(logger/error ~@args))

(defmacro spy [expression]
    `(let [result# ~expression]
         (warn (quote ~expression) " => " result#)
         result#))
