(ns com.ben-allred.code-review-bot.ui.services.store.core
    (:require [com.ben-allred.collaj.core :as collaj]
              [com.ben-allred.collaj.enhancers :as collaj.enhancers]
              [com.ben-allred.code-review-bot.ui.services.store.reducers :as reducers]
              [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.logging :as log :include-macros true]))

(defonce ^:private store (collaj/create-custom-store r/atom
                         reducers/root
                         collaj.enhancers/with-fn-dispatch
                         (collaj.enhancers/with-log-middleware #(log/spy %) identity)))

(def get-state (:get-state store))

(def dispatch (:dispatch store))
