(ns com.ben-allred.code-review-bot.ui.views.components.modal
    (:require [com.ben-allred.code-review-bot.ui.utils.core :as utils]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private hide-modal []
    (store/dispatch actions/hide-modal))

(defn modal [{{:keys [state content title]} :modal}]
    [:div.modal-wrapper
     {:class-name (name state)
      :on-click   hide-modal}
     (when (not= :unmounted state)
         [:div.modal
          {:on-click #(.stopPropagation %)}
          [:div.modal-title-bar
           [:i.fa.fa-times.spacer]
           (when title
               [:div.modal-title title])
           [:i.fa.fa-times.button {:on-click hide-modal}]]
          [:div.modal-content
           content]])])
