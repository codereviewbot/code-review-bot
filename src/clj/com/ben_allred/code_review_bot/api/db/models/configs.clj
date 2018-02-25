(ns com.ben-allred.code-review-bot.api.db.models.configs
    (:require [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.api.services.mongo :as mongo]
              [com.ben-allred.code-review-bot.utils.uuids :as uuids]
              [clojure.set :as set]))

(defn ^:private keywordify [rules]
    (for [[key conditions] rules]
        [(keyword key) (for [[path condition] conditions]
                           [(map keyword path) condition])]))

(def ^:private response-keys #{:id :description :messages :rules :repo-url :slack-path})

(def ^:private update-keys (disj response-keys :id :repo-url))

(defn ^:private transform [config]
    (-> config
        (maps/update-maybe :messages maps/update-all set)
        (maps/update-maybe :rules keywordify)
        (set/rename-keys {:_id :id})
        (select-keys response-keys)))

(def ^:private find-one
    (comp transform (partial mongo/find-one :configs)))

(defn ^:private find-many [query]
    (map transform (mongo/find-many :configs query)))

(defn find-by-repo [repo-url]
    (find-one {:repo-url repo-url}))

(defn find-by-id [id]
    (find-one {:_id (uuids/->uuid id)}))

(defn find-by-repos [repo-urls]
    (when (seq repo-urls)
        (find-many {:repo-url {:$in repo-urls}})))

(defn update-by-id [id config]
    (->> (select-keys config update-keys)
        (assoc {} :$set)
        (mongo/update :configs {:_id (uuids/->uuid id)})
        (transform)))

(defn save [config]
    (let [config (assoc config :_id (uuids/random))]
        (try
            (mongo/insert :configs config)
            config
            (catch Exception e
                nil))))
