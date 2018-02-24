(ns com.ben-allred.code-review-bot.ui.views.auth
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn login [state]
    [:div
     [:h2 "login"]
     [:div
      [:a {:href (nav/path-for :home)} "go home"]]])

(defn logout-button []
    [:button.pure-button.pure-button-primary
     {:on-click (fn []
                    (.pushState (.-history js/window) nil nil "/auth/logout")
                    (nav/reload!))}
     "logout"])
