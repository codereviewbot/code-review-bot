(ns com.ben-allred.code-review-bot.ui.services.store.reducers
    (:require [com.ben-allred.collaj.reducers :as collaj.reducers]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn page
    ([] nil)
    ([state [type page]]
     (case type
         :router/navigate page
         state)))

(defn repo
    ([] [])
    ([state [type repo]]
     (case type
         :repo/request :requesting
         :repo/fail :failed
         :repo/succeed (:data repo)
         state)))

(defn repos
    ([] [])
    ([state [type repos]]
     (case type
         :repos/request :requesting
         :repos/fail :failed
         :repos/succeed (:data repos)
         state)))

(defn user
    ([] nil)
    ([state [type user]]
        (case type
            :user/request :requesting
            :user/fail :failed
            :user/succeed (:data user)
            state)))

(def root
    (collaj.reducers/combine (maps/->map page repo repos user)))
