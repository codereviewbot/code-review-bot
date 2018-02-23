(ns com.ben-allred.code-review-bot.api.services.integrations.github
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [clojure.set :as set]
              [com.ben-allred.code-review-bot.utils.strings :as strings]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.services.http :as http]
              [clojure.core.async :as async]
              [com.ben-allred.code-review-bot.services.env :as env]))

(def ^:private config
    {:user-agent  (env/get :user-agent)
     :auth-header (str "token " (env/get :github-token))})

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
     :action       action
     :status       (:state pull-request)
     :pull-request (-> pull-request
                       (select-keys [:url :merged? :merged-at :state :title :merged-by :user])
                       (assoc :sha (get-in pull-request [:head :sha]))
                       (assoc :diff (second (async/<!! (http/get (:diff-url pull-request))))))})

(defn ^:private push-payload [{:keys [ref head-commit]}]
    {:event  :push
     :branch (ref->branch ref)
     :commit (select-keys head-commit [:committer :author :url :message :timestamp])})

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
                (assoc payload :github)))))

(def post-comment
    (reify integrations/IIntegrator
        (process [_ payload]
            (when-let [message (get-in payload [:rules :message])]
                (let [pull-request (get-in payload [:github :pull-request])
                      url          (str (:url pull-request) "/reviews")]
                    (http/post url {:headers {"User-Agent"    (:user-agent config)
                                              "Authorization" (:auth-header config)}
                                    :body    {:commit_id (:sha pull-request)
                                              :body      message
                                              :event     "COMMENT"
                                              :comments  []}})))
            payload)))
