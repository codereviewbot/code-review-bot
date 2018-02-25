(ns com.ben-allred.code-review-bot.ui.views.repos
    (:require [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.ui.services.store.actions :as actions]
              [com.ben-allred.code-review-bot.ui.views.components.core :as components]
              [com.ben-allred.code-review-bot.ui.services.navigation :as nav]
              [com.ben-allred.code-review-bot.ui.views.components.form :as form]
              [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.ui.utils.core :as utils]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.strings :as strings]
              [cljs.core.async :as async]))

(defn ^:private key->label [key]
    (string/join " " (string/split (strings/upper-first (name key)) #"-")))

(defn ^:private succeed! []
    (async/<! (store/dispatch actions/request-configs))
    (store/dispatch actions/hide-modal))

(defn repo-field [attrs repo key]
    (let [label (key->label key)]
        [:div.field
         (utils/classes {key true})
         [:label.label label]
         [form/input
          (:tag attrs :input)
          (get @repo key)
          (-> attrs
              (assoc :on-change #(swap! repo assoc key %))
              (dissoc :tag))]]))

(defn ^:private repo-form [{:keys [repo-url id description]} action]
    (let [repo       (r/atom {:repo-url repo-url :description description})
          auto-focus (if id :description :repo-url)]
        (fn [repo-data action]
            [:form.repo-form
             {:on-submit #(do
                              (.preventDefault %)
                              (async/go
                                  (let [[_ _ status] (async/<! (store/dispatch (action @repo)))]
                                      (case status
                                          :created (succeed!)
                                          :ok (succeed!)
                                          :conflict (store/dispatch (actions/show-toast :error "Repo configuration already exists. Contact the repo's owner for access."))
                                          :bad-request (store/dispatch (actions/show-toast :error "All fields must be filled out."))
                                          (store/dispatch (actions/show-toast :error "An unknown error has occrred. Please try again later."))))))}
             [repo-field
              {:disabled   id
               :tab-index  1
               :auto-focus (= auto-focus :repo-url)}
              repo
              :repo-url]
             (when-not id
                 [repo-field {:tab-index 2 :auto-focus false} repo :slack-path])
             [repo-field
              {:tag        :textarea
               :tab-index  3
               :auto-focus (= auto-focus :description)}
              repo
              :description]
             [:div
              [:button.pure-button.pure-button-primary.button
               "Save"]]])))

(defn ^:private repo [{:keys [repo-url id description] :as repo}]
    [:div.repo-url
     [:i.fa.fa-pencil.button
      {:on-click #(store/dispatch
                      (actions/show-modal
                          [repo-form repo (comp (partial actions/update-description id) :description)]
                          "Edit project"))}]
     " "
     [:a {:href (nav/path-for :repo {:repo-id id})}
      (or description repo-url)]])

(defn root [state]
    (store/dispatch actions/request-configs)
    (fn [{:keys [configs] :as state}]
        (let [available? (= :available (:status configs))]
            [:div
             [:h2 "Your projects"]
             [:button.pure-button.pure-button-primary.button
              {:on-click #(store/dispatch
                              (actions/show-modal
                                  [repo-form {} actions/save-repo]
                                  "Create project"))}
              [:i.fa.fa-plus-circle]]
             (cond
                 (and available? (seq (:data configs)))
                 [:ul
                  (for [config (:data configs)]
                      [:li {:key (:id config)}
                       [repo config]])]

                 available?
                 [:div "You have no projects configured"]

                 :else
                 [components/spinner])])))
