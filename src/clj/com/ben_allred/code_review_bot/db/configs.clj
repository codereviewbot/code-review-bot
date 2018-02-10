(ns com.ben-allred.code-review-bot.db.configs
    (:require [com.ben-allred.code-review-bot.utils.maps :as maps]
              [com.ben-allred.code-review-bot.services.mongo :as mongo]))

(defn ^:private keywordify [rules]
    (for [[key conditions] rules]
        [(keyword key) (for [[path condition] conditions]
                           [(map keyword path) condition])]))

(defn ^:private transform [document]
    (-> document
        (maps/update-maybe :messages maps/update-all set)
        (maps/update-maybe :rules keywordify)))

(def find-one
    (comp transform (partial mongo/find-one :configs)))
