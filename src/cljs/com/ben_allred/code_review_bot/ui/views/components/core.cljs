(ns com.ben-allred.code-review-bot.ui.views.components.core
    (:refer-clojure :exclude [keyword vector list pr-str set hash-map]))


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
