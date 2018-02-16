(ns com.ben-allred.code-review-bot.ui.views.rules
    (:require [com.ben-allred.code-review-bot.ui.views.components.core :as components]))

(defn display [rules]
    [:div.rules
     [:h3 "Rules:"]
     [:ul.rule
      (let [last-idx (dec (count rules))]
          (for [[idx [result conditions]] (map vector (range) rules)]
              [:li.rule-text {:key (pr-str {:result result :conditions conditions})}
               [:span
                {:style {:font-size :larger :font-weight :bold}}
                [components/keyword result]]
               " happens"
               [:ul.conditions
                (for [[idx [path condition]] (map vector (range) conditions)]
                    [:li {:key (pr-str {:result result :path path :condition condition})}
                     (if (zero? idx)
                         "when "
                         " and ")
                     [components/vector (map (comp #(vary-meta % assoc :key (gensym)) components/keyword) path)]
                     " matches "
                     [components/pr-str condition]])]
               [:div.button-row
                (when-not (zero? idx)
                    [:button.pure-button.button-warning
                     [:i.fa.fa-arrow-up]])
                (when (not= idx last-idx)
                    [:button.pure-button.button-warning
                     [:i.fa.fa-arrow-down]])
                [:button.pure-button.button-error
                 [:i.fa.fa-minus-circle]]]]))]
     [:div.button-row
      [:button.pure-button.pure-button-primary
       [:i.fa.fa-plus-circle]]]])
