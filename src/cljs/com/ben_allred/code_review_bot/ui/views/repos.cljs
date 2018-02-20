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
    (store/dispatch actions/request-configs)
    (fn [{:keys [configs] :as state}]
        (let [available? (= :available (:status configs))]
            [:div
             [:h2 "Your Projects"]
             (cond
                 (and available? (seq (:data configs)))
                 [:ul
                  (for [config (:data configs)]
                      [:li {:key (:id config)}
                       [repo config]])]

                 available?
                 [:div "You have no repos configured"]

                 :else
                 [components/spinner])])))
