(ns com.ben-allred.code-review-bot.api.db.models.users
    (:require [com.ben-allred.code-review-bot.api.services.mongo :as mongo]
              [clojure.set :as set]))

(defn find-by-github-user [github-user]
    (-> :users
        (mongo/find-one {:github-user github-user})
        (set/rename-keys {:_id :id})))
