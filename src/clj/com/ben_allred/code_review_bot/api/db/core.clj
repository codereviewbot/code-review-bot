(ns com.ben-allred.code-review-bot.api.db.core
    (:require [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]))

(defn config-by-user [user repo-id]
    (some->> user
        (:repos)
        (configs/find-by-repos)
        (filter (comp (partial = repo-id) str :id))
        (first)))

(defn configs-by-user [user]
    (some->> user
        (:repos)
        (configs/find-by-repos)
        (map #(select-keys % [:id :description :repo-url]))))
