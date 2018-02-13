(ns com.ben-allred.code-review-bot.db.core
    (:require [com.ben-allred.code-review-bot.db.models.users :as users]
              [com.ben-allred.code-review-bot.db.models.configs :as configs]))

(defn ^:private find-user-repos [email]
    (->> email
        (users/find-by-email)
        (:repos)
        (configs/find-by-repos)))

(defn find-repos-for-user-by-email [email]
    (->> email
        (find-user-repos)
        (map #(select-keys % [:id :description]))))

(defn find-repo-for-user-by-email [email repo-id]
    (->> email
        (find-user-repos)
        (filter (comp (partial = repo-id) str :id))
        (first)))
