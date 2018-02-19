(ns com.ben-allred.code-review-bot.services.env
    #?(:clj (:require [environ.core :as environ])))

(def env
    #?(:clj  environ/env
       :cljs {}))

(def dev?
    #?(:clj (not= "production" (:ring-env env))
       :cljs (boolean (re-find #"localhost" (.-hostname (.-location js/window))))))
