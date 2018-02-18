(ns com.ben-allred.code-review-bot.api.services.integrations.github
    (:require [com.ben-allred.code-review-bot.api.services.integrations.core :as integrations]
              [clojure.set :as set]
              [com.ben-allred.code-review-bot.utils.strings :as strings]
              [clojure.string :as string]))

(defn ^:private ref->branch [ref]
    (-> ref
        (str)
        (string/split #"\/")
        (last)
        (strings/trim-to-nil)))

(defn ^:private push-payload [{:keys [ref head_commit repository]}]
    {:branch     (ref->branch ref)
     :commit     (select-keys head_commit [:committer :author :url :message :timestamp])
     :repository (-> repository
                     (select-keys [:description :url :updated_at :pushed_at :full_name])
                     (set/rename-keys {:updated_at :updated-at
                                       :pushed_at :pushed-at
                                       :full_name :full-name}))})

(def integrator
    (reify integrations/IIntegrator
        (process [_ payload]
            (->> payload
                (:body)
                (push-payload)
                (assoc payload :github)))))
