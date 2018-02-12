(ns com.ben-allred.code-review-bot.ui.views.auth
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn login [state]
    [:div
     [:h2 "login"]
     [:div
      [nav/link {:page :home} "go home"]]])

(defn logout-button []
    [:button
     {:on-click (fn []
                    (.pushState (.-history js/window) nil nil "/auth/logout")
                    (nav/reload!))}
     "logout"])
