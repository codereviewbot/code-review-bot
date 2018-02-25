(ns com.ben-allred.code-review-bot.ui.views.components.toast
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.utils.core :as utils]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [clojure.string :as string]))

(defn toast [messages]
    [:div.toast-container
     [:ul.toast-messages
      (for [[key {:keys [text level]}] (sort-by :key messages)]
          [:li.toast-message
           (-> {:key key}
               (utils/classes {level true}))
           [:div.toast-text text]
           [:i.fa.fa-times.button
            {:on-click #(store/dispatch [:toast/remove key])}]])]])
