(ns com.ben-allred.code-review-bot.ui.views.home
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn root [state]
    [:div
     [:h2 "home"]
     [:div
      [:a {:href (nav/path-for :repos)} "/repos"]]])
