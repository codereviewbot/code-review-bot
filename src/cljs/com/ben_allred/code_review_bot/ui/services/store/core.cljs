(ns com.ben-allred.code-review-bot.ui.services.store.core
    (:require [com.ben-allred.collaj.core :as collaj]
              [com.ben-allred.collaj.enhancers :as collaj.enhancers]
              [com.ben-allred.code-review-bot.ui.services.store.reducers :as reducers]
              [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.maps :as maps :include-macros true]))

(def ^:private store (collaj/create-custom-store r/atom
                         reducers/root
                         collaj.enhancers/with-fn-dispatch
                         (collaj.enhancers/with-log-middleware (comp js/console.log pr-str))))

(def get-state (:get-state store))

(def dispatch (:dispatch store))
