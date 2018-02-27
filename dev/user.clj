(ns user
    (:require [figwheel-sidecar.repl-api :as f]
              [monger.core :as mg]
              [com.ben-allred.code-review-bot.services.env :as env]
              [com.ben-allred.code-review-bot.api.services.mongo]
              [monger.core :as mg]))

(defn cljs-repl []
    (f/cljs-repl))

(defn update-env [key val]
    (alter-var-root #'com.ben-allred.code-review-bot.services.env/get assoc key val))

(defn connect-to-remote-db []
    (alter-var-root #'com.ben-allred.code-review-bot.api.services.mongo/db
        (let [conn-str (str (:remote-mongodb-uri env/get) "?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500;safe=false&w=1;wtimeoutms=5000;fsync=true")]
            (constantly (delay (:db (mg/connect-via-uri conn-str)))))))

(defn connect-to-local-db []
    (alter-var-root #'com.ben-allred.code-review-bot.api.services.mongo/db
        (let [conn-str (str (:mongodb-uri env/get) "?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500;safe=false&w=1;wtimeoutms=5000;fsync=true")]
            (constantly (delay (:db (mg/connect-via-uri conn-str)))))))
