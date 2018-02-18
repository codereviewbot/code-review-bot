(ns com.ben-allred.code-review-bot.ui.services.store.actions
    (:require [com.ben-allred.code-review-bot.services.http :as http]
              [cljs.core.async :as async]))

(defn ^:private request* [request dispatch success-type error-type]
    (async/go
        (let [[status result] (async/<! request)]
            (dispatch (if (= :success status)
                          [success-type result]
                          [error-type result])))))

(def request-user-details
    (fn [[dispatch]]
        (dispatch [:user/request])
        (request* (http/get "/auth/details") dispatch :user/succeed :user/fail)))

(def request-configs
    (fn [[dispatch]]
        (dispatch [:configs/request])
        (-> "/api/configs"
            (http/get)
            (request* dispatch :configs/succeed :configs/fail))))

(defn request-config [config-id]
    (fn [[dispatch]]
        (dispatch [:config/request])
        (-> "/api/configs/"
            (str config-id)
            (http/get)
            (request* dispatch :config/succeed :config/fail))))

(defn update-config [config-id config]
    (fn [[dispatch]]
        (dispatch [:config/update])
        (-> "/api/configs/"
            (str config-id)
            (http/patch {:body {:data config}})
            (request* dispatch :config/succeed :config/fail))))
