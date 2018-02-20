(ns com.ben-allred.code-review-bot.ui.views.components.core
    (:refer-clojure :exclude [keyword vector list pr-str set hash-map])
    (:require [markdown-to-hiccup.core :as md]
              [com.ben-allred.code-review-bot.services.emoji :as emoji]
              [goog.string :as gstring]))

(defn ^:private fix-string [s]
    (-> s
        (emoji/replace-emojis)
        (gstring/unescapeEntities)))

(defn ^:private fix-hiccup [[tag attrs & children]]
    (cond-> [({:i :em :em :strong :p :span} tag tag)]
        (seq attrs) (conj attrs)
        (seq children) (into (->> children
                                 (map #(if (vector? %)
                                           (fix-hiccup %)
                                           (fix-string %)))))))

(defn spinner []
    [:div.loader
     "Loading..."])

(defn spinner-overlay [show? component]
    (if show?
        [:div
         {:style {:position :relative}}
         [:div
          {:style {:position :absolute :min-height "100%" :min-width "100%"}}
          [spinner]]
         [:div
          {:style {:position :absolute :min-height "100%" :min-width "100%" :background-color "rgba(0,0,0,0.25)"}}
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
     (-> value
         (md/md->hiccup)
         (get-in [3 2] [:span ""])
         (fix-hiccup))])
