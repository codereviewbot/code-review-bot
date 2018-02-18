(ns com.ben-allred.code-review-bot.ui.app
    (:require [com.ben-allred.code-review-bot.ui.services.navigation :as nav]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.services.http :as http]
              [com.ben-allred.code-review-bot.ui.views.auth :as auth]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.views.error :as error]
              [com.ben-allred.code-review-bot.ui.views.home :as home]
              [com.ben-allred.code-review-bot.ui.views.repo :as repo]
              [com.ben-allred.code-review-bot.ui.views.repos :as repos]
              [com.ben-allred.code-review-bot.utils.logging :as log :include-macros true]
              [reagent.core :as r]))

(enable-console-print!)

(defn on-js-reload [])

(def components
    {:home  home/root
     :login auth/login
     :repos repos/root
     :repo  repo/root})

(defn app []
    (store/dispatch actions/request-user-details)
    (fn []
        (let [{:keys [user] :as state} (store/get-state)
              component (components (get-in state [:page :handler]) error/not-found)]
            [:div.app
             (case (:status user)
                 :pending [components/spinner]
                 :error [error/not-logged-in]
                 [:div
                  [:header
                   [:h1 "Code Review Bot"]
                   [auth/logout-button]]
                  [:main.main
                   [component state]]])])))

(r/render
    [app]
    (.getElementById js/document "app"))
