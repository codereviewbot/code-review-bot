(ns com.ben-allred.code-review-bot.ui.views.components.core
    (:refer-clojure :exclude [keyword vector list pr-str set hash-map]))


(defn spinner []
    [:div.loader "Loading..."])

(defn vector [coll]
    (-> [:span.code.vector [:span.code.brace "["]]
        (into (interpose " " coll))
        (conj [:span.code.brace "]"])))

(defn keyword [value]
    [:span.code.keyword (str (clojure.core/keyword value))])

(defn pr-str [value]
    [:span.code.pr-str (clojure.core/pr-str value)])
