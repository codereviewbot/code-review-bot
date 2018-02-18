(ns com.ben-allred.code-review-bot.ui.views.components.form
    (:require [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.ui.services.events :as events]
              [com.ben-allred.code-review-bot.ui.services.transformations :as transformations]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private input* [{:keys [on-change] :as attrs}]
    [:input
     (-> attrs
         (assoc :auto-focus true)
         (maps/update-maybe :on-change comp #(.-value (.-target %)))
         (maps/update-maybe :on-key-down comp events/code->key #(.-keyCode %)))])

(defn input [initial-value attrs]
    (let [value (r/atom nil)]
        (r/create-class
            {:component-did-mount
             (fn [] (reset! value initial-value))
             :reagent-render
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
                             :always (dissoc :on-submit :on-cancel))])})))

(defn editable [value attrs component]
    (let [editing?      (r/atom false)
          stop-editing! #(reset! editing? false)]
        (fn [value {:keys [on-submit transformer] :as attrs} component]
            (if @editing?
                (let [transformed (transformations/to-view transformer value)]
                    [input
                     transformed
                     (-> attrs
                         (assoc :on-cancel stop-editing!
                                :on-submit (comp (fn [next-value]
                                                     (stop-editing!)
                                                     (when (and on-submit
                                                               next-value
                                                               (not= value next-value))
                                                         (on-submit next-value)))
                                               (partial transformations/to-model transformer)))
                         (assoc-in [:style :min-width] (str (+ (* 8 (count transformed)) 50) "px"))
                         (dissoc :transformer))])
                [:span.editable.button
                 {:on-click #(reset! editing? true)}
                 component]))))
