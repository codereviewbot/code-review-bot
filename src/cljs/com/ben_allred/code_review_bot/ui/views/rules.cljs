(ns com.ben-allred.code-review-bot.ui.views.rules
    (:require [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.utils.colls :as colls]
              [com.ben-allred.code-review-bot.ui.views.components.form :as form]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.utils.keywords :as keywords]
              [com.ben-allred.code-review-bot.ui.services.transformations :as transformations]))

(defn ^:private update-rules [repo-id rules]
    (->> rules
        (actions/update-rules repo-id)
        (store/dispatch)))

(defn ^:private move [repo-id rules from to]
    (update-rules repo-id (colls/swap rules from to)))

(defn ^:private delete [repo-id rules from]
    (update-rules repo-id (colls/exclude rules from)))

(defn ^:private delete-in [repo-id rules path]
    (update-rules repo-id
        (colls/update-in rules (butlast path) colls/exclude (last path))))

(defn ^:private add-rule [repo-id rules]
    (update-rules repo-id
        (colls/append rules [:nothing ()])))

(defn ^:private add-condition [repo-id rules idx]
    (update-rules repo-id
        (colls/update-in rules [idx 1] colls/append [() ""])))

(defn ^:private move-up [repo-id rules idx]
    (move repo-id rules idx (dec idx)))

(defn ^:private move-down [repo-id rules idx]
    (move repo-id rules idx (inc idx)))

(defn ^:private change-result [repo-id rules idx new-result]
    (->> rules
        (map-indexed (fn [idx' [_ conditions :as existing]]
                         (if (= idx idx')
                             [new-result conditions]
                             existing)))
        (update-rules repo-id)))

(defn ^:private change-in [repo-id rules path new-value]
    (->> new-value
        (colls/assoc-in rules path)
        (update-rules repo-id)))

(defn path [repo-id rules idx idx' path]
    [form/editable
     path
     {:on-submit   (partial change-in repo-id rules [idx 1 idx' 0])
      :transformer (transformations/vector transformations/keyword)}
     [components/vector
      (for [[idx'' k] (map-indexed vector path)]
          ^{:key (pr-str [idx idx' idx'' k])}
          [components/keyword k])]])

(defn condition [repo-id rules idx idx' data-path match]
    [:li
     [:i.fa.fa-minus-circle.red.button
      {:on-click #(delete-in repo-id rules [idx 1 idx'])}]
     (if (zero? idx')
         " when "
         " and ")
     [path repo-id rules idx idx' data-path]
     " matches "
     [form/editable
      match
      {:on-submit (partial change-in repo-id rules [idx 1 idx' 1])}
      [components/pr-str match]]])

(defn button-row [repo-id rules idx]
    (let [last-idx (dec (count rules))]
        [:div.button-row
         (when-not (zero? idx)
             [:button.pure-button.button-warning
              {:on-click (fn [] (move-up repo-id rules idx))}
              [:i.fa.fa-arrow-up]])
         (when (not= idx last-idx)
             [:button.pure-button.button-warning
              {:on-click (fn [] (move-down repo-id rules idx))}
              [:i.fa.fa-arrow-down]])
         [:button.pure-button.button-error
          {:on-click (fn [] (delete repo-id rules idx))}
          [:i.fa.fa-minus-circle]]]))

(defn rule [conditions repo-id rules idx result]
    (let [add-button [:i.fa.fa-plus-circle.button.blue
                      {:on-click #(add-condition repo-id rules idx)}]]
        [:li.rule-text
         [form/editable
          result
          {:on-submit   (partial change-result repo-id rules idx)
           :transformer transformations/keyword}
          [:span.result
           [components/keyword result]]]
         " happens "
         (when (seq conditions)
             add-button)
         (if (empty? conditions)
             "always. "
             [:ul.conditions
              (for [[idx' [path match]] (map-indexed vector conditions)]
                  ^{:key (pr-str [result path match])}
                  [condition repo-id rules idx idx' path match])])
         (when (empty? conditions)
             add-button)
         [button-row repo-id rules idx]]))

(defn display [repo-id rules]
    [:div.rules
     [:h3 "Rules:"]
     [:ul.rule
      (for [[idx [result conditions]] (map-indexed vector rules)]
          ^{:key (pr-str [result conditions])}
          [rule conditions repo-id rules idx result])]
     [:div.button-row
      [:button.pure-button.pure-button-primary
       {:on-click #(add-rule repo-id rules)}
       [:i.fa.fa-plus-circle]]]])
