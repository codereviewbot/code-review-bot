(ns com.ben-allred.code-review-bot.services.http
    (:refer-clojure :exclude [get])
    (:require [com.ben-allred.code-review-bot.utils.json :as json]
              [com.ben-allred.code-review-bot.utils.keywords :as keywords]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.utils.transit :as transit]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [kvlt.chan :as kvlt]
              [com.ben-allred.code-review-bot.services.content :as content]
        #?(:clj  [clojure.core.async :as async]
           :cljs [cljs.core.async :as async])))

(def ^:private status->kw
    {200 :ok
     201 :created
     202 :accepted
     204 :no-content
     400 :bad-request
     401 :unauthorized
     404 :not-found
     409 :conflict
     424 :failed-dependency
     500 :internal-server-error
     501 :not-implemented
     503 :service-unavailable
     504 :gateway-timeout})

(def ^:private success?
    (comp #{200 201 202 204} :status))

(def ^:private content-type
    #?(:clj  "application/json"
       :cljs "application/transit"))

(defn content-type-header [{:keys [headers]}]
    (clojure.core/get headers "content-type" (:content-type headers)))

(defn ^:private request* [method url request]
    (async/go
        (let [{:keys [status] :as response} (async/<! (-> request
                                                          (assoc :method method :url url)
                                                          (content/prepare content-type)
                                                          (kvlt/request!)))
              body   (-> response
                         (content/parse (content-type-header response))
                         (:body))
              status (status->kw status status)]
            (if (success? response)
                [:success body status response]
                [:error body status response]))))

(defn get [url]
    (request* :get url nil))

(defn post [url request]
    (request* :post url request))

(defn patch [url request]
    (request* :patch url request))
