(ns com.ben-allred.code-review-bot.ui.views.repo
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]))

(defn root [state]
    (store/dispatch (actions/request-repo (get-in state [:page :route-params :repo-id])))
    (fn [{{:keys [description repo-url] :as repo} :repo :as state}]
        [:div
         [:h2 "Repo"]
         [:div description]
         [:div [:a {:href repo-url}
                repo-url]]]))