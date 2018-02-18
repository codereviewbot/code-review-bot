(ns com.ben-allred.code-review-bot.ui.views.repo
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.views.rules :as rules]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]))

(defn root [state]
    (let [repo-id (get-in state [:page :route-params :repo-id])]
        (store/dispatch (actions/request-config (get-in state [:page :route-params :repo-id])))
        (fn [{{{:keys [description rules repo-url]} :data status :status} :config :as state}]
            [:div
             [:h2 "Repo"]
             [:div description]
             [:div [:a {:href repo-url}
                    repo-url]]
             [components/spinner-overlay
              (not= :available status)
              [rules/display repo-id rules]]])))
