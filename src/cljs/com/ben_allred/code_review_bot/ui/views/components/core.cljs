(ns com.ben-allred.code-review-bot.ui.views.components.core
    (:refer-clojure :exclude [keyword vector list pr-str set hash-map])
    (:require [markdown-to-hiccup.core :as md]
              [com.ben-allred.code-review-bot.services.emoji :as emoji]
              [goog.string :as gstring]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private fix-string [s]
    (-> s
        (gstring/unescapeEntities)
        (emoji/replace-emojis)))

(defn ^:private fix-hiccup [[tag attrs & children]]
    (cond-> [({:i :em :em :strong :p :span} tag tag)]
        :always (conj (assoc attrs :key (gensym)))
        (seq children) (into (->> children
                                 (map #(if (vector? %)
                                           (fix-hiccup %)
                                           (fix-string %)))))))

(defn spinner []
    [:div.loader])

(defn spinner-overlay [show? component]
    (if show?
        [:div
         {:style {:position :relative}}
         [:div
          {:style {:position :absolute :height "50%" :min-height "200px" :min-width "100%"}}
          [spinner]]
         [:div
          {:style {:position :absolute :height "100%" :min-height "400px" :min-width "100%" :background-color "rgba(0,0,0,0.25)"}}
          ""]
         component]
        component))

(defn vector [coll]
    (-> [:span.code.vector [:span.code.brace "["]]
        (into (interpose " " coll))
        (conj [:span.code.brace "]"])))

(defn keyword [value]
    [:span.code.keyword (str (clojure.core/keyword value))])

(defn pr-str [value]
    [:span.code.pr-str (clojure.core/pr-str value)])

(defn markdown [value]
    [:span.code.markdown
     (let [hiccup (-> value
                      (md/md->hiccup)
                      (get 3)
                      (nnext)
                      (or [:span ""]))]
         (fix-hiccup (into [:span {}] hiccup)))])
