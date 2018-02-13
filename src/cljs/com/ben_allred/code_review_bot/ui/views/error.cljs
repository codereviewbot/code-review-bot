(ns com.ben-allred.code-review-bot.ui.views.error
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn not-logged-in []
    [:div "Try "
     [:a
      {:href     "/auth/login"
       :on-click (fn [] (nav/reload!))}
      "logging in"]
     ", douchenozzle"])

(defn not-found [state]
    [:div
     [:h2 "not-found"]
     [:div
      [nav/link {:page :home} "go home"]]])