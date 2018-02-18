(ns com.ben-allred.code-review-bot.api.db.core
    (:require [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]))

(defn ^:private find-user-configs [email]
    (->> email
        (users/find-by-email)
        (:repos)
        (configs/find-by-repos)))

(defn find-configs-for-user-by-email [email]
    (->> email
        (find-user-configs)
        (map #(select-keys % [:id :description]))))

(defn find-config-for-user-by-email [email repo-id]
    (->> email
        (find-user-configs)
        (filter (comp (partial = repo-id) str :id))
        (first)))
