(ns com.ben-allred.code-review-bot.ui.services.store.reducers
    (:require [com.ben-allred.collaj.reducers :as collaj.reducers]
              [com.ben-allred.code-review-bot.utils.maps :as maps]))

(defn ^:private page
    ([] nil)
    ([state [type page]]
     (case type
         :router/navigate page
         state)))

(defn ^:private messages
    ([] {:status :init :data nil})
    ([state [type config]]
     (case type
         :config/request (assoc state :status :pending)
         :config.messages/update (assoc state :status :pending)
         :config/fail (assoc state :status :error)
         :config.messages/fail (assoc state :status :error)
         :config/succeed {:status :available :data (get-in config [:data :messages])}
         :config.messages/succeed {:status :available :data (get-in config [:data :messages])}
         state)))

(defn ^:private rules
    ([] {:status :init :data nil})
    ([state [type config]]
     (case type
         :config/request (assoc state :status :pending)
         :config.rules/update (assoc state :status :pending)
         :config/fail (assoc state :status :error)
         :config.rules/fail (assoc state :status :error)
         :config/succeed {:status :available :data (get-in config [:data :rules])}
         :config.rules/succeed {:status :available :data (get-in config [:data :rules])}
         state)))

(defn ^:private config*
    ([] {:status :init :data nil})
    ([state [type config]]
     (case type
         :config/request (assoc state :status :pending)
         :config/fail (assoc state :status :error)
         :config/succeed {:status :available :data (dissoc (:data config) :rules :messages)}
         state)))

(def ^:private config (collaj.reducers/assoc-in config* [:data :rules] rules [:data :messages] messages))

(defn ^:private configs
    ([] {:status :init :data nil})
    ([state [type configs]]
     (case type
         :configs/request (assoc state :status :pending)
         :configs/fail (assoc state :status :error)
         :configs/succeed {:status :available :data (:data configs)}
         state)))

(defn ^:private user
    ([] {:status :init :data nil})
    ([state [type user]]
     (case type
         :user/request (assoc state :status :pending)
         :user/fail (assoc state :status :error)
         :user/succeed {:status :available :data (:data user)}
         state)))

(defn ^:private modal
    ([] {:state :unmounted})
    ([state [type content title]]
     (case type
         :modal/mount {:state :mounted :content content :title title}
         :modal/show (assoc state :state :shown)
         :modal/hide (assoc state :state :modal-hidden)
         :modal/unmount {:state :unmounted}
         state)))

(def root
    (collaj.reducers/combine {:page page :config config :configs configs :user user :modal modal}))
