(ns com.ben-allred.code-review-bot.ui.utils.http
    (:refer-clojure :exclude [get])
    (:require [cljs-http.client :as client]
              [cljs.core.async :as async :include-macros true]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]
              [com.ben-allred.code-review-bot.utils.transit :as transit]))

(def ^:private success?
    (comp #{200 201 202 204} :status))

(defn ^:private with-transit-headers [request]
    (update request :headers assoc "content-type" "application/transit" "accept" "application/transit"))

(defn get [url success-cb error-cb]
    (async/go
        (let [response (async/<! (client/get url (with-transit-headers {})))
              cb (if (success? response) success-cb error-cb)]
            (cb (transit/parse (:body response))))))

(defn patch [url data success-cb error-cb]
    (async/go
        (let [response (async/<! (client/patch url (-> data (with-transit-headers) (update :body transit/stringify))))
              cb (if (success? response) success-cb error-cb)]
            (cb (transit/parse (:body response))))))
