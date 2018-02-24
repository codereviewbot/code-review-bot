(ns com.ben-allred.code-review-bot.ui.services.store.actions
    (:require [com.ben-allred.code-review-bot.services.http :as http]
              [cljs.core.async :as async]
              [com.ben-allred.code-review-bot.ui.utils.macros :as macros :include-macros true]))

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

(defn update-rules [config-id rules]
    (fn [[dispatch]]
        (dispatch [:config.rules/update])
        (-> "/api/configs/"
            (str config-id)
            (http/patch {:body {:data {:rules rules}}})
            (request* dispatch :config.rules/succeed :config.rules/fail))))

(defn update-messages [config-id messages]
    (fn [[dispatch]]
        (dispatch [:config.messages/update])
        (-> "/api/configs/"
            (str config-id)
            (http/patch {:body {:data {:messages messages}}})
            (request* dispatch :config.messages/succeed :config.messages/fail))))

(defn show-modal [content & [title]]
    (fn [[dispatch]]
        (dispatch [:modal/mount content title])
        (macros/after 1 (dispatch [:modal/show]))))

(defn hide-modal []
    (fn [[dispatch]]
        (dispatch [:modal/hide])
        (macros/after 600 (dispatch [:modal/unmount]))))
