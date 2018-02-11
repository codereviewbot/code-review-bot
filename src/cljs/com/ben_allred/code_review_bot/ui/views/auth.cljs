(ns com.ben-allred.code-review-bot.ui.views.auth
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn login [state]
    [:div
     [:h2 "login"]
     [:div
      [nav/link {:page :home} "go home"]]])
