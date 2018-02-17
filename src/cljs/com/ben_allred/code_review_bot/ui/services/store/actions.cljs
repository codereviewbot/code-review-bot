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

(def request-repos
    (fn [[dispatch]]
        (dispatch [:repos/request])
        (-> "/api/repos"
            (http/get)
            (request* dispatch :repos/succeed :repos/fail))))

(defn request-repo [repo-id]
    (fn [[dispatch]]
        (dispatch [:repo/request])
        (-> "/api/repos/"
            (str repo-id)
            (http/get)
            (request* dispatch :repo/succeed :repo/fail))))

(defn update-repo [repo-id repo]
    (fn [[dispatch]]
        (dispatch [:repo/update])
        (-> "/api/repos/"
            (str repo-id)
            (http/patch {:body repo})
            (request* dispatch :repo/succeed :repo/fail))))
