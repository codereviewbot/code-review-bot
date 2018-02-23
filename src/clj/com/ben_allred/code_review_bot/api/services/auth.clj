(ns com.ben-allred.code-review-bot.api.services.auth
    (:require [ring.util.response :as resp]
              [com.ben-allred.code-review-bot.services.env :as env]
              [clojure.core.async :as async]
              [com.ben-allred.code-review-bot.services.http :as http]
              [com.ben-allred.code-review-bot.api.services.jwt :as jwt]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.uuids :as uuids]
              [com.ben-allred.code-review-bot.utils.query-params :as qp]
              [clojure.set :as set]
              [com.ben-allred.code-review-bot.utils.json :as json]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.api.services.integrations.github :as github]))

(def ^:private oauth-config
    {:client-id     (env/get :oauth-client-id)
     :client-secret (env/get :oauth-client-secret)
     :code-url      "https://github.com/login/oauth/authorize"
     :token-url     "https://github.com/login/oauth/access_token"
     :user-url      "https://api.github.com/user"
     :redirect-url  (str (env/get :base-url) "/auth/callback")
     :scope         ["user" "repo"]
     :user-agent    (env/get :user-agent)})

(defn ^:private token->cookie [resp value]
    (->> value
        (jwt/encode)
        (assoc {:path "/" :http-only true} :value)
        (assoc-in resp [:cookies "auth-token"])))

(defn ^:private when-successful [response]
    (when (= :success (first response))
        (second response)))

(defn ^:private get-access-token [query-params]
    (-> (http/post (:token-url oauth-config)
            {:body {:client_id     (:client-id oauth-config)
                    :client_secret (:client-secret oauth-config)
                    :code          (get query-params "code")
                    :redirect_uri  (:redirect-url oauth-config)
                    :state         (get query-params "state")}})
        (async/<!!)))

(defn ^:private get-user-details [access]
    (-> (http/get (:user-url oauth-config)
            {:headers {"Authorization" (str "token " (:access_token access))
                       "User-Agent"    (:user-agent oauth-config)}})
        (async/<!!)))

(defn ^:private authenticate [user]
    (-> (str (env/get :base-url) "/")
        (resp/redirect)
        (token->cookie user)))

(defn login []
    (let [auth-user (env/get :auth-user)]
        (if-let [user (and auth-user (json/parse auth-user))]
            (authenticate user)
            (->> {:client_id    (:client-id oauth-config)
                  :redirect_url (:redirect-url oauth-config)
                  :scope        (string/join " " (:scope oauth-config))
                  :state        (uuids/random)}
                (qp/stringify)
                (str (:code-url oauth-config) "?")
                (resp/redirect)))))

(defn logout []
    (-> (str (env/get :base-url) "/")
        (resp/redirect)
        (assoc :cookies {"auth-token" {:value "" :path "/" :http-only true :max-age 0}})))

(defn callback [query-params]
    (let [user (some-> query-params
                   (get-access-token)
                   (when-successful)
                   (get-user-details)
                   (when-successful)
                   (select-keys [:login :email :name]))]
        (if (seq user)
            (authenticate user)
            {:status 403})))
