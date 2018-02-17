(ns com.ben-allred.code-review-bot.utils.logging
    (:require [taoensso.timbre :as logger :include-macros true]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.strings :as strings]
              [com.ben-allred.code-review-bot.utils.colors :as colors]
              [kvlt.core :refer [quiet!]]))

(defmacro debug [& args]
    `(logger/debug ~@args))

(defmacro info [& args]
    `(logger/info ~@args))

(defmacro warn [& args]
    `(logger/warn ~@args))

(defmacro error [& args]
    `(logger/error ~@args))

(defmacro spy* [expr f spacer]
    `(let [result# ~expr]
         (warn (quote ~expr) ~spacer (colors/colorize (~f result#)))
         result#))

(defmacro spy [expr]
    `(spy* ~expr identity "\uD83D\uDC40 "))

(defmacro spy-tap [f expr]
    `(spy* ~expr ~f "\uD83C\uDF7A "))

(defn ^:private ns-color [ns-str]
    (colors/with-style ns-str {:color :blue :trim? true}))

(defn ^:private level-color [level]
    (->> (case level
             :debug :cyan
             :warn :yellow
             :error :red
             :white)
        (assoc {:attribute :invert :trim? true} :color)
        (colors/with-style (str "[" (string/upper-case (name level)) "]"))))

(defn ^:private no-color [arg]
    (if-not (colors/colorized? arg)
        (colors/with-style arg {})
        arg))

(defn ^:private formatter [{:keys [env level ?ns-str] :as data}]
    (update data :vargs (fn [vargs]
                            (conj #?(:clj  (seq vargs)
                                     :cljs (seq (map no-color vargs)))
                                (level-color level)
                                (ns-color (or ?ns-str "ns?"))))))

(quiet!)

(logger/merge-config!
    {:level      :debug
     :middleware [formatter]
     :appenders  {:println    {:enabled? false}
                  :console    {:enabled? false}
                  :system-out {:enabled? true
                               :fn       #?(:clj  #(.println System/out @(:msg_ %))
                                            :cljs #(apply (.-log js/console) (colors/prep-cljs (interpose (no-color "") (:vargs %)))))}}})
