(ns com.ben-allred.code-review-bot.api.routes.configs
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.api.db.core :as db]
              [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private sans [config]
    (dissoc config :slack-path))

(defroutes configs
    (GET "/" req
        (if-let [configs (-> req
                             (get-in [:user :login])
                             (db/configs-by-github-user))]
            {:status 200
             :body   {:data (map sans configs)}}
            {:status 404}))
    (GET "/:config-id" {:keys [params] :as req}
        (if-let [config (-> (get-in req [:user :login])
                            (db/config-by-github-user (:config-id params)))]
            {:status 200
             :body   {:data (sans config)}}
            {:status 404}))
    (PATCH "/:config-id" {:keys [params body] :as req}
        (let [config-id (:config-id params)
              config    (configs/find-by-id config-id)
              access?   (-> (get-in req [:user :login])
                            (users/find-by-github-user)
                            (:repos)
                            (set)
                            (contains? (:repo-url config)))]
            (cond
                access? {:status 200 :body {:data (sans (configs/update-by-id config-id (:data body)))}}
                (nil? config) {:status 404}
                :else {:status 401}))))
