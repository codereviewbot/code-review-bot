(ns com.ben-allred.code-review-bot.api.routes.hooks
    (:use compojure.core)
    (:require [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.api.services.integrations.rules :as rules]
              [com.ben-allred.code-review-bot.api.services.integrations.slack :as slack]
              [com.ben-allred.code-review-bot.api.services.integrations.github :as github]
              [com.ben-allred.code-review-bot.api.db.models.configs :as configs]
              [com.ben-allred.code-review-bot.api.services.integrations.config :as config]
              [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [clojure.core.async :as async]
              [clojure.string :as string]))

(defn ^:private format-dt [value]
    (let [date-string (-> "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                          (clj-time.format/formatter)
                          (clj-time.format/unparse (clj-time.coerce/to-date-time value)))
          pos (- (count date-string) 2)]
        (str (subs date-string 0 pos) ":" (subs date-string pos))))

(defn ^:private idiomatize [value]
    (cond
        (map? value) (->> value
                         (map (fn [[k v]]
                                  (let [k' (name k)]
                                      [(keyword (str (string/replace k' #"_" "-") (when (boolean? v) "?")))
                                       (if (string/ends-with? k' "_at")
                                           (format-dt v)
                                           (idiomatize v))])))
                         (into {}))
        (coll? value) (map idiomatize value)
        :else value))

(defn ^:private handle-integration [payload]
    (->> payload
        (idiomatize)
        (integrations/process config/look-up)
        (integrations/process github/generate-payload)
        (integrations/process rules/match-rule)
        (integrations/process slack/post)
        (integrations/process github/post-comment)))

(def webhooks
    (POST "/git" req
        (async/thread
            (handle-integration (select-keys req [:body])))
        {:status 202}))
