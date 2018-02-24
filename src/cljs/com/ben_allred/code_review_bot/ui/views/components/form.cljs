(ns com.ben-allred.code-review-bot.ui.views.components.form
    (:require [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.ui.services.events :as events]
              [com.ben-allred.code-review-bot.ui.services.transformations :as transformations]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.ui.utils.core :as utils]))

(defn ^:private input* [tag {:keys [on-change] :as attrs}]
    [tag
     (-> attrs
         (utils/classes {:input true})
         (update :auto-focus #(if (nil? %) true %))
         (maps/update-maybe :on-change comp #(.-value (.-target %)))
         (maps/update-maybe :on-key-down comp events/->key-code))])

(defn input
    ([initial-value attrs]
     [input :input initial-value attrs])
    ([tag initial-value attrs]
     (let [value (r/atom nil)]
         (r/create-class
             {:component-did-mount
              (fn [] (reset! value initial-value))
              :reagent-render
              (fn [tag initial-value {:keys [on-submit on-cancel on-change]}]
                  [input* tag (cond-> attrs
                                  :always (assoc :type :text
                                                 :value @value
                                                 :on-key-down #(case %
                                                                   :esc (and on-cancel (on-cancel))
                                                                   :enter (and on-submit (on-submit @value))
                                                                   nil))
                                  on-change (update :on-change juxt #(reset! value %))
                                  (not on-change) (assoc :on-change #(reset! value %))
                                  on-submit (assoc :on-blur #(on-submit @value))
                                  :always (dissoc :on-submit :on-cancel))])}))))

(defn editable [value attrs component]
    (let [editing?      (r/atom false)
          stop-editing! #(reset! editing? false)]
        (fn [value {:keys [on-submit transformer] :as attrs} component]
            (if @editing?
                (let [transformed (transformations/to-view transformer value)]
                    [input
                     :textarea
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
                         (dissoc :transformer))])
                [:span.editable.button
                 {:on-click #(reset! editing? true)}
                 component]))))
