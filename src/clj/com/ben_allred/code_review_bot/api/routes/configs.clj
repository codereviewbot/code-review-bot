(ns com.ben-allred.code-review-bot.api.routes.configs
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.api.db.core :as db]
              [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.api.utils.response :as response]))

(defn ^:private sans [config]
    (dissoc config :slack-path))

(defroutes configs
    (GET "/" req
        (if-let [configs (-> req
                             (get-in [:user :login])
                             (db/configs-by-github-user))]
            (response/respond [:ok {:data (map sans configs)}])
            (response/respond [:not-found])))
    (GET "/:config-id" {:keys [params] :as req}
        (if-let [config (-> (get-in req [:user :login])
                            (db/config-by-github-user (:config-id params)))]
            (response/respond [:ok {:data (sans config)}])
            (response/respond [:not-found])))
    (PATCH "/:config-id" {:keys [params body] :as req}
        (let [config-id (:config-id params)
              config    (configs/find-by-id config-id)
              access?   (-> (get-in req [:user :login])
                            (users/find-by-github-user)
                            (:repos)
                            (set)
                            (contains? (:repo-url config)))]
            ;(Thread/sleep 1500)
            (cond
                access? (response/respond [:ok {:data (sans (configs/update-by-id config-id (:data body)))}])
                (nil? config) (response/respond [:not-found])
                :else (response/respond [:unauthorized])))))
