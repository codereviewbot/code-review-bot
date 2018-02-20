(ns com.ben-allred.code-review-bot.ui.views.repo
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.views.rules :as rules]
              [com.ben-allred.code-review-bot.ui.views.messages :as messages]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn root [state]
    (let [repo-id (get-in state [:page :route-params :repo-id])]
        (store/dispatch (actions/request-config (get-in state [:page :route-params :repo-id])))
        (fn [{{{:keys [description rules messages repo-url]} :data} :config :as state}]
            (get-in state [:config :data])
            [:div
             [:h2 "Repo"]
             [:div description]
             [:div [:a {:href repo-url}
                    repo-url]]
             [:div.config
              [components/spinner-overlay
               (not= :available (:status rules))
               [rules/display repo-id (:data rules)]]
              [components/spinner-overlay
               (not= :available (:status messages))
               [messages/display repo-id (:data messages)]]]])))
