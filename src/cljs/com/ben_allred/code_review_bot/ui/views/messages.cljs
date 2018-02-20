(ns com.ben-allred.code-review-bot.ui.views.messages
    (:require [com.ben-allred.code-review-bot.ui.views.components.form :as form]
              [com.ben-allred.code-review-bot.ui.services.transformations :as transformations]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [clojure.set :as set]))

(defn ^:private update-messages [messages repo-id]
    (->> messages
        (actions/update-messages repo-id)
        (store/dispatch)))

(defn ^:private delete-message [repo-id messages key message]
    (-> messages
        (update key disj message)
        (update-messages repo-id)))

(defn ^:private edit-message [repo-id messages key old-message new-message]
    (-> messages
        (update key (comp #(disj % old-message) #(conj % new-message)))
        (update-messages repo-id)))

(defn ^:private add-message [repo-id messages key]
    (-> messages
        (update key conj "EDIT ME")
        (update-messages repo-id)))

(defn ^:private edit-key [repo-id messages old-key new-key]
    (-> messages
        (set/rename-keys {old-key new-key})
        (update-messages repo-id)))

(defn ^:private delete-message-set [repo-id messages key]
    (-> messages
        (dissoc key)
        (update-messages repo-id)))

(defn ^:private add-message-set [repo-id messages]
    (-> messages
        (assoc :EDIT-ME #{})
        (update-messages repo-id)))

(defn ^:private compare-to [special direction]
    (fn [key-1 key-2]
        (cond
            (= special key-1) (* 1 direction)
            (= special key-2) (* -1 direction)
            :else (compare key-1 key-2))))

(defn ^:private msg [repo-id messages message-key message]
    [:li.message-str
     [:i.fa.fa-minus-circle.red.button
      {:on-click #(delete-message repo-id messages message-key message)}]
     [form/editable
      message
      {:on-submit #(edit-message repo-id messages message-key message %)}
      [:span.message
       [components/markdown message]]]])

(defn ^:private message [repo-id messages message-key message-set]
    (let [add-button [:i.fa.fa-plus-circle.button.blue
                      {:on-click #(add-message repo-id messages message-key)}]]
        [:li.message-pair
         [form/editable
          message-key
          {:on-submit   #(edit-key repo-id messages message-key %)
           :transformer transformations/keyword}
          [:span.message-key
           [components/keyword message-key]]]
         " is associated with "
         (when (seq message-set)
             add-button)
         (if (empty? message-set)
             "no messages."
             [:ul.message-list
              (for [message-str (sort (compare-to "EDIT ME" -1) message-set)]
                  ^{:key message-str}
                  [msg repo-id messages message-key message-str])])
         (when (empty? message-set)
             add-button)
         [:div.button-row
          [:button.pure-button.button-error
           {:on-click #(delete-message-set repo-id messages message-key)}
           [:i.fa.fa-minus-circle.button]]]]))

(defn display [repo-id messages]
    [:div.messages
     [:h3 "Messages:"]
     [:ul.message-pairs
      (for [[message-key message-set] (sort-by key (compare-to :EDIT-ME 1) messages)]
          ^{:key (str message-key)}
          [message repo-id messages message-key message-set])]
     [:div.button-row
      [:button.pure-button.pure-button-primary
       {:on-click #(add-message-set repo-id messages)}
       [:i.fa.fa-plus-circle]]]])
