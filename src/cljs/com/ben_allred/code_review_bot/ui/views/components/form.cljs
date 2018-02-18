(ns com.ben-allred.code-review-bot.ui.views.components.form
    (:require [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.ui.services.events :as events]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private input* [attrs]
    [:input
     (-> attrs
         (assoc :focus true)
         (maps/update-maybe :on-change comp #(.-value (.-target %)))
         (maps/update-maybe :on-key-down comp #(events/code->key (.-keyCode %))))])

(defn input [initial-value attrs]
    (let [value (r/atom initial-value)]
        (fn [initial-value {:keys [on-submit on-cancel]}]
            [input* (cond-> attrs
                        :always (assoc :type :text
                                       :value @value
                                       :on-change #(reset! value %)
                                       :on-key-down #(case %
                                                         :esc (and on-cancel (on-cancel))
                                                         :enter (and on-submit (on-submit @value))
                                                         nil))
                        on-submit (assoc :on-blur #(on-submit @value))
                        :always (dissoc :on-submit :on-cancel))])))

(defn editable [value attrs component]
    (let [editing? (r/atom false)
          stop-editing! #(do (reset! editing? false) %)]
        (fn [value attrs component]
            (if @editing?
                [input
                 value
                 (-> attrs
                     (assoc :on-cancel stop-editing!)
                     (maps/update-maybe :on-submit comp stop-editing!))]
                [:span.editable
                 {:on-click #(reset! editing? true)
                  :style {:cursor :pointer}}
                 component]))))
