(ns com.ben-allred.code-review-bot.ui.utils.http
    (:refer-clojure :exclude [get])
    (:require [cljs-http.client :as client]
              [cljs.core.async :as async :include-macros true]
              [com.ben-allred.code-review-bot.utils.logging :as log]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]))

(def ^:private success?
    (comp #{200 201 202 204} :status))

(defn get [url success-cb error-cb]
    (async/go
        (let [response (async/<! (client/get url))
              cb (if (success? response) success-cb error-cb)]
            (cb (:body response)))))

(defn patch [url data success-cb error-cb]
    (async/go
        (let [response (async/<! (client/patch url data))
              cb (if (success? response) success-cb error-cb)]
            (cb (:body response)))))
