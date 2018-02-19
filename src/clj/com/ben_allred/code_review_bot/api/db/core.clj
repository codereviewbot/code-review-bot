(ns com.ben-allred.code-review-bot.api.db.core
    (:require [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]))

(defn ^:private find-user-configs [github-user]
    (->> github-user
        (users/find-by-github-user)
        (:repos)
        (configs/find-by-repos)))

(defn config-by-github-user [github-user repo-id]
    (->> github-user
        (find-user-configs)
        (filter (comp (partial = repo-id) str :id))
        (first)))

(defn configs-by-github-user [github-user]
    (->> github-user
        (find-user-configs)
        (map #(select-keys % [:id :description]))))
