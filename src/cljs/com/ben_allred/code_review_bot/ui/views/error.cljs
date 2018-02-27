(ns com.ben-allred.code-review-bot.ui.views.error
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]
              [com.ben-allred.code-review-bot.ui.views.main :as main]))

(defn not-logged-in []
    [:div
     [main/header]
     [:div.main
      [:p "Manage your Slack / Github webhook integrations"]
      [:button.pure-button.pure-button-primary.button
       {:on-click #(nav/go-to! "/auth/login")}
       [:i.fa.fa-github]
       " login"]]])

(defn not-found [state]
    [:div
     [main/header (= :available (get-in state [:user :status]))]
     [:h2 "Page not found"]
     [:div
      "Try going "
      [:a {:href (nav/path-for :home)} "home"]]])
