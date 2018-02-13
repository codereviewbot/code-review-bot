(ns com.ben-allred.code-review-bot.db.models.users
    (:require [com.ben-allred.code-review-bot.services.mongo :as mongo]))

(defn find-by-email [email]
    (mongo/find-one :users {:email email}))
