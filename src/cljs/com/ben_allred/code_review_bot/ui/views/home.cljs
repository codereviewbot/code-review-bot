(ns com.ben-allred.code-review-bot.ui.views.home
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn root [state]
    [:div
     [:h2 "home"]
     [:div
      [nav/link {:page :login} "login"]]])
