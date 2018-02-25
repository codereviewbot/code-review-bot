(ns com.ben-allred.code-review-bot.api.db.models.users
    (:require [com.ben-allred.code-review-bot.api.services.mongo :as mongo]
              [clojure.set :as set]))

(defn ^:private transform [user]
    (-> user
        (set/rename-keys {:_id :id})
        (update :repos set)))

(defn find-by-github-user [github-user]
    (->> github-user
        (assoc {} :github-user)
        (mongo/find-one :users)
        (transform)))

(defn update-by-id [id user]
    (->> (select-keys user [:repos])
        (assoc {} :$set)
        (mongo/update :users {:_id id})))
