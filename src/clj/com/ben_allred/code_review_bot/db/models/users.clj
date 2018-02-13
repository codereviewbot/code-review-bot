(ns com.ben-allred.code-review-bot.db.models.users
    (:require [com.ben-allred.code-review-bot.services.mongo :as mongo]
              [clojure.set :as set]))

(defn find-by-email [email]
    (-> :users
        (mongo/find-one {:email email})
        (set/rename-keys {:_id :id})))
