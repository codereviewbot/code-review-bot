(ns com.ben-allred.code-review-bot.ui.services.transformations
    (:refer-clojure :exclude [keyword vector])
    (:require [cljs.reader :as edn]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [clojure.string :as string]))

(defprotocol ITransform
    (to-view [this value])
    (to-model [this value]))

(extend-protocol ITransform
    nil
    (to-view [_ value]
        value)
    (to-model [_ value]
        value))

(def keyword
    (reify ITransform
        (to-view [_ value]
            (str value))
        (to-model [_ value]
            (let [[colon & value] (str value)]
                (when (= ":" colon)
                    (clojure.core/keyword (apply str value)))))))

(defn vector [transformer]
    (reify ITransform
        (to-view [_ value]
            (str "["
                (->> value
                    (map (partial to-view transformer))
                    (interpose " ")
                    (apply str))
                "]"))
        (to-model [_ value]
            (try
                (let [result (edn/read-string value)]
                     (when (vector? result)
                         (mapv (partial to-model transformer) result)))
                (catch js/Object e
                    nil)))))

(def markdown
    (reify ITransform
        (to-view [_ value]
            (-> value
                (string/replace #"\n" "\\n")
                (string/replace #"^>[\s]+" ">")))
        (to-model [_ value]
            (-> value
                (string/replace #"\\n" "\n")
                (string/replace #"^>([^\s])" "> $1")))))
