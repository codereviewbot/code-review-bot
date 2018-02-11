(ns com.ben-allred.code-review-bot.ui.views.not-found
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(defn root [state]
    [:div
     [:h2 "not-found"]
     [:div
      [nav/link {:page :home} "go home"]]])
