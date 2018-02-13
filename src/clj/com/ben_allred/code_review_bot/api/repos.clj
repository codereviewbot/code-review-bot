(ns com.ben-allred.code-review-bot.api.repos
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.db.core :as db]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defroutes repos
    (GET "/" req
        (if-let [repos (-> req
                           (get-in [:user :email])
                           (db/find-repos-for-user-by-email))]
            {:status 200
             :body   {:data repos}}
            {:status 404})))
