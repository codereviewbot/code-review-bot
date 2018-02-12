(ns com.ben-allred.code-review-bot.ui.services.store.reducers
    (:require [com.ben-allred.collaj.reducers :as collaj.reducers]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn page
    ([] nil)
    ([state [type page]]
     (case type
         :router/navigate page
         state)))

(defn user
    ([] nil)
    ([state [type user]]
        (case type
            :user/request :requesting
            :user/fail :failed
            :user/succeed user
            state)))

(def root
    (collaj.reducers/combine (maps/->map page user)))
