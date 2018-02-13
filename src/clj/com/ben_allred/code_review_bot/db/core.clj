(ns com.ben-allred.code-review-bot.db.core
    (:require [com.ben-allred.code-review-bot.db.models.users :as users]
              [com.ben-allred.code-review-bot.db.models.configs :as configs]))

(defn find-repos-for-user-by-email [email]
    (map #(select-keys % [:_id :description]) (-> email
                  (users/find-by-email)
                  (:repos)
                  (configs/find-by-repos))))
