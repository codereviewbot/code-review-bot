(ns com.ben-allred.code-review-bot.api.services.integrations.github
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [clojure.set :as set]
              [com.ben-allred.code-review-bot.utils.strings :as strings]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.services.http :as http]
              [clojure.core.async :as async]))

(defn ^:private ref->branch [ref]
    (-> ref
        (str)
        (string/split #"\/")
        (last)
        (strings/trim-to-nil)))

(defn ^:private common [{:keys [repository]}]
    {:repository (-> repository
                     (select-keys [:description :url :updated-at :pushed-at :full-name]))})

(defn ^:private pull-request-payload [{:keys [action pull-request]}]
    {:event        :pull-request
     :status       (:state pull-request)
     :pull-request (-> pull-request
                       (select-keys [:merged? :merged-at :state :title :merged-by :user])
                       (assoc :diff (second (async/<!! (http/get (:diff-url pull-request))))))})

(defn ^:private push-payload [{:keys [ref head-commit]}]
    {:event      :push
     :branch     (ref->branch ref)
     :commit     (select-keys head-commit [:committer :author :url :message :timestamp])})

(defn ^:private body->payload [body]
    (if (and (:action body) (:pull-request body))
        (pull-request-payload body)
        (push-payload body)))

(def generate-payload
    (reify integrations/IIntegrator
        (process [_ payload]
            (->> payload
                (:body)
                (body->payload)
                (merge (common (:body payload)))
                (assoc payload :github)
                (log/spy-tap :github)))))

(def post-comment
    (reify integrations/IIntegrator
        (process [_ payload]
            (log/spy-tap :rules payload))))
