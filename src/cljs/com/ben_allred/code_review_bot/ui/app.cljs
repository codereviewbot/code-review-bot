(ns com.ben-allred.code-review-bot.ui.app
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.utils.logging :as log]
              [com.ben-allred.code-review-bot.ui.views.home :as home]
              [com.ben-allred.code-review-bot.ui.views.not-found :as not-found]
              [com.ben-allred.code-review-bot.ui.views.auth :as auth]
              [reagent.core :as r]))

(enable-console-print!)

(defn on-js-reload [])

(def components
    {:home  home/root
     :login auth/login})

(defn app []
    (let [state     (store/get-state)
          component (components (get-in state [:page :handler]) not-found/root)]
        [:div
         {:style {:margin "8px"}}
         [:h1 "Code Review Bot"]
         [component state]]))

(r/render
    [app]
    (.getElementById js/document "app"))
