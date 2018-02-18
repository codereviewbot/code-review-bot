(ns com.ben-allred.code-review-bot.ui.services.store.reducers
    (:require [com.ben-allred.collaj.reducers :as collaj.reducers]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn page
    ([] nil)
    ([state [type page]]
     (case type
         :router/navigate page
         state)))

(defn config
    ([] {:status :init :data nil})
    ([state [type config]]
     (case type
         :config/request (assoc state :status :pending)
         :config/update (assoc state :status :pending)
         :config/fail (assoc state :status :error)
         :config/succeed {:status :available :data (:data config)}
         state)))

(defn configs
    ([] {:status :init :data nil})
    ([state [type configs]]
     (case type
         :configs/request (assoc state :status :pending)
         :configs/fail (assoc state :status :error)
         :configs/succeed {:status :available :data (:data configs)}
         state)))

(defn user
    ([] {:status :init :data nil})
    ([state [type user]]
        (case type
            :user/request (assoc state :status :pending)
            :user/fail (assoc state :status :error)
            :user/succeed {:status :available :data (:data user)}
            state)))

(def root
    (collaj.reducers/combine {:page page :config config :configs configs :user user}))
