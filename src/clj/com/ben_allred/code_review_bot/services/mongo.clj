(ns com.ben-allred.code-review-bot.services.mongo
    (:refer-clojure :exclude [find update])
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [com.ben-allred.code-review-bot.utils.env :as env]
              [clojure.tools.reader :as reader]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn ^:private conn-str [uri]
    (str uri "?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500&autoConnectRetry=true;safe=false&w=1;wtimeout=2500;fsync=true"))

(defonce ^:private db
    (delay
        (let [db (:db (mg/connect-via-uri (conn-str (:mongodb-uri env/env))))]
            (mc/ensure-index db :configs {:repo-url 1} {:unique true})
            db)))

(def object-id monger.conversion/to-object-id)

(def find
    (partial mc/find-maps @db))

(def find-one
    (partial mc/find-one-as-map @db))

(def insert
    (partial mc/insert @db))

(def update
    (partial mc/update @db))
