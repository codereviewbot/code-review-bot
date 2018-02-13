(ns com.ben-allred.code-review-bot.ui.views.repos
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]))

(defn root [state]
    (store/dispatch actions/request-repos)
    (fn [state]
        [:div "REPOS"]))
