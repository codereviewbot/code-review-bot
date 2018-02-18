(ns com.ben-allred.code-review-bot.ui.views.rules
    (:require [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.utils.colls :as colls]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private move [repo-id rules from to]
    (let [rules' (if (nil? to)
                     (colls/exclude rules from)
                     (colls/swap rules from to))]
        (->> {:rules rules'}
            (actions/update-config repo-id)
            (store/dispatch))))

(defn ^:private move-up [repo-id rules idx]
    (move repo-id rules idx (dec idx)))

(defn ^:private move-down [repo-id rules idx]
    (move repo-id rules idx (inc idx)))

(defn display [repo-id rules]
    [:div.rules
     [:h3 "Rules:"]
     [:ul.rule
      (let [last-idx (dec (count rules))]
          (for [[idx [result conditions]] (map vector (range) rules)]
              [:li.rule-text {:key (pr-str [result conditions])}
               [:span
                {:style {:font-size :larger :font-weight :bold}}
                [components/keyword result]]
               " happens"
               (if (empty? conditions)
                   " always."
                   [:ul.conditions
                    (for [[idx [path condition]] (map vector (range) conditions)]
                        [:li {:key (pr-str [result path condition])}
                         (if (zero? idx)
                             "when "
                             " and ")
                         [components/vector (map-indexed #(with-meta [components/keyword %2] {:key %1}) path)]
                         " matches "
                         [components/pr-str condition]])])
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
                 {:on-click (fn [] (move repo-id rules idx nil))}
                 [:i.fa.fa-minus-circle]]]]))]
     [:div.button-row
      [:button.pure-button.pure-button-primary
       [:i.fa.fa-plus-circle]]]])
