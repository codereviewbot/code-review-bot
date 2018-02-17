(ns com.ben-allred.code-review-bot.api.routes.repos
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.api.db.core :as db]
              [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]))

(defroutes repos
    (GET "/" req
        (if-let [repos (-> req
                           (get-in [:user :email])
                           (db/find-repos-for-user-by-email))]
            {:status 200
             :body   {:data repos}}
            {:status 404}))
    (GET "/:repo-id" {:keys [params] :as req}
        (if-let [repo (-> (get-in req [:user :email])
                          (db/find-repo-for-user-by-email (:repo-id params)))]
            {:status 200
             :body   {:data repo}}
            {:status 404}))
    (PATCH "/:repo-id" {:keys [params] :as req}
        (let [repo    (configs/find-by-id (:repo-id params))
              access? (-> (get-in req [:user :email])
                          (users/find-by-email)
                          (:repos)
                          (set)
                          (contains? (:repo-url repo)))]
            (cond
                access? {:status 200 :body {:data (assoc repo :pretend-i've-been-updated true)}}
                (nil? repo) {:status 404}
                :else {:status 401}))))
