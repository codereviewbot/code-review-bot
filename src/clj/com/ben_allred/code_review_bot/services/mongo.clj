(ns com.ben-allred.code-review-bot.services.mongo
    (:refer-clojure :exclude [find update])
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [com.ben-allred.code-review-bot.utils.env :as env]
              [clojure.tools.reader :as reader]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn ^:private conn-str [uri]
    (str uri "?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500&autoConnectRetry=true;safe=false&w=1;wtimeout=2500;fsync=true"))

(defonce ^:private db (:db (mg/connect-via-uri (conn-str (:mongodb-uri env/env)))))

(mc/ensure-index db :configs {:repo-url 1} {:unique true})

(defn ^:private keywordify [rules]
    (for [[key conditions] rules]
        [(keyword key) (for [[path condition] conditions]
                           [(map keyword path) condition])]))

(defn ^:private transform [document]
    (-> document
        (maps/update-maybe :messages maps/update-all set)
        (maps/update-maybe :rules keywordify)))

(def object-id monger.conversion/to-object-id)

(defn find [query]
    (map transform (mc/find-maps db :configs query)))

(defn find-one [query]
    (transform (mc/find-one-as-map db :configs query)))

(defn insert [document]
    (mc/insert db :configs document))

(defn update [query document]
    (mc/update db :configs query document))
