(ns com.ben-allred.code-review-bot.services.content
    (:require [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.utils.json :as json]
              [com.ben-allred.code-review-bot.utils.transit :as transit]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defn ^:private with-headers [request type]
    (update request :headers assoc "Content-type" type "Accept" type))

(def ^:private json?
    (comp (partial re-find #"application/json") str))

(def ^:private transit?
    (comp (partial re-find #"application/transit") str))

(defn parse [data content-type]
    (cond-> data
        (json? content-type)
        (maps/update-maybe :body json/parse)

        (transit? content-type)
        (maps/update-maybe :body transit/parse)))

(defn prepare [data accept]
    (cond-> data
        (json? accept) (->
                           (maps/update-maybe :body json/stringify)
                           (with-headers "application/json"))
        (transit? accept) (->
                              (maps/update-maybe :body transit/stringify)
                              (with-headers "application/transit"))))
