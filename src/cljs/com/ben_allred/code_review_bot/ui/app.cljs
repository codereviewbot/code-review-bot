(ns com.ben-allred.code-review-bot.ui.app
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.views.home :as home]
              [com.ben-allred.code-review-bot.ui.views.error :as error]
              [com.ben-allred.code-review-bot.ui.views.auth :as auth]
              [com.ben-allred.code-review-bot.utils.logging :as log :include-macros true]
              [com.ben-allred.code-review-bot.ui.utils.http :as http]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [reagent.core :as r]
              [com.ben-allred.code-review-bot.ui.services.navigation :as nav]))

(enable-console-print!)

(defn on-js-reload [])

(def components
    {:home  home/root
     :login auth/login})

(defn with-logged-in-status [state component]
    (store/dispatch actions/request-user-details)
    (fn [{:keys [user] :as state} component]
        (case user
            :requesting [components/spinner]
            :failed [error/not-logged-in]
            [:div
             [:header [auth/logout-button]]
             [component state]])))

(defn app []
    (let [state     (store/get-state)
          component (components (get-in state [:page :handler]) error/not-found)]
        [:div.app
         [:h1 "Code Review Bot"]
         [with-logged-in-status state component]]))

(r/render
    [app]
    (.getElementById js/document "app"))
