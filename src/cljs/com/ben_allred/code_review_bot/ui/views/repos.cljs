(ns com.ben-allred.code-review-bot.ui.views.repos
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.navigation :as nav]
              [com.ben-allred.code-review-bot.ui.views.components.form :as form]))

(defn ^:private repo-form [repo-data]
    (let []))

(defn ^:private repo [{:keys [repo-url id description] :as repo}]
    [:div repo-url
     [:i.fa.fa-pencil.button
      {:on-click #(store/dispatch
                      (actions/show-modal
                          [:div "...edit project form"]
                          "Edit project"))}]
     " "
     [:a {:href (nav/path-for :repo {:repo-id id})}
      (or description repo-url)]])

(defn root [state]
    (store/dispatch actions/request-configs)
    (fn [{:keys [configs] :as state}]
        (let [available? (= :available (:status configs))]
            [:div
             [:h2 "Your projects"]
             [:button.pure-button.pure-button-primary.button
              {:on-click #(store/dispatch
                              (actions/show-modal
                                 [:div "...create project form"]
                                  "Create project"))}
              [:i.fa.fa-plus-circle]]
             (cond
                 (and available? (seq (:data configs)))
                 [:ul
                  (for [config (:data configs)]
                      [:li {:key (:id config)}
                       [repo config]])]

                 available?
                 [:div "You have no projects configured"]

                 :else
                 [components/spinner])])))
