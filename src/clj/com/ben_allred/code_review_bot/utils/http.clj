(ns com.ben-allred.code-review-bot.utils.http
    (:require [clj-http.client :as client]
              [com.ben-allred.code-review-bot.utils.json :as json]
              [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.utils.keywords :as keywords]))

(defn post [url params]
    (client/post url (-> params
                         (maps/update-maybe :body json/stringify)
                         (maps/update-maybe :headers (partial maps/map-keys keywords/safe-name)))))
