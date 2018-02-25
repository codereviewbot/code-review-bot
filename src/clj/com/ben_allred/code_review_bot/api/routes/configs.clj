(ns com.ben-allred.code-review-bot.api.routes.configs
    (:use [compojure.core])
    (:require [com.ben-allred.code-review-bot.api.db.core :as db]
              [com.ben-allred.code-review-bot.api.db.models.users :as users]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.api.utils.response :as response]
              [com.ben-allred.code-review-bot.utils.strings :as strings]))

(def ^:private config-keys #{:slack-path :repo-url :description})

(defn ^:private sans [config]
    (dissoc config :slack-path))

(defn ^:private validate-not-empty [m ks]
    (->> ks
        (reduce
            #(let [v (get m %2)]
                 (and %1 (string? v) (strings/trim-to-nil v)))
            true)))

(defroutes configs
    (POST "/" req
        (let [data   (select-keys (get-in req [:body :data]) config-keys)
              valid? (validate-not-empty data config-keys)
              config (when valid?
                         (some-> req
                             (:user)
                             (db/save-config-for-user data)))]
            (response/respond (cond
                                  (not valid?) [:bad-request]
                                  (not config) [:conflict]
                                  :else [:created {:data config}]))))
    (GET "/" req
        (if-let [configs (-> req
                             (:user)
                             (db/configs-by-user))]
            (response/respond [:ok {:data (map sans configs)}])
            (response/respond [:not-found])))
    (GET "/:config-id" {:keys [params] :as req}
        (if-let [config (-> req
                            (:user)
                            (db/config-by-user (:config-id params)))]
            (response/respond [:ok {:data (sans config)}])
            (response/respond [:not-found])))
    (PATCH "/:config-id" {:keys [params body] :as req}
        (let [config-id (:config-id params)
              config    (configs/find-by-id config-id)
              access?   (-> req
                            (:user)
                            (:repos)
                            (contains? (:repo-url config)))]
            (cond
                access? (response/respond [:ok {:data (sans (configs/update-by-id config-id (:data body)))}])
                (nil? config) (response/respond [:not-found])
                :else (response/respond [:unauthorized])))))
