(ns com.ben-allred.code-review-bot.ui.views.main
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]
              [com.ben-allred.code-review-bot.ui.views.auth :as auth]
              [com.ben-allred.code-review-bot.ui.utils.core :as utils]))

(defn header [logged-in?]
    [:header.header
     [:div.home-link
      (-> (when logged-in?
              {:on-click #(nav/navigate! :home)})
          (utils/classes {:button logged-in?}))
      [:img.logo {:src "/images/logo.png"}]
      [:h1 "Code Review Bot"]]
     [:div
      (when logged-in?
          [auth/logout-button])]])
