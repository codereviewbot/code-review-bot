(ns com.ben-allred.code-review-bot.utils.env
    (:require [environ.core :as env]))

(def env env/env)

(def server-port
    (if-let [port (:port env)]
        (Integer/parseInt (str port))
        3000))

(def nrepl-port 7000)
