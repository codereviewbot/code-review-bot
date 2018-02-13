(ns com.ben-allred.code-review-bot.ui.views.repos
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn repo [{:keys [repo-url id description]}]
    [:div repo-url
     [:a {:href (nav/path-for :repo {:repo-id id})}
      (or description repo-url)]])

(defn root [state]
    (store/dispatch actions/request-repos)
    (fn [{:keys [repos] :as state}]
        [:div
         [:h2 "Your Projects"]
         (if (keyword? repos)
             [components/spinner]
             [:ul
              (for [repo-data repos]
                  [:li {:key (:id repo-data)}
                   [repo repo-data]])])]))
