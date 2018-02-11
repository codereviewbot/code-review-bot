(ns com.ben-allred.code-review-bot.ui.services.store.core
    (:require [com.ben-allred.collaj.core :as collaj]
              [com.ben-allred.collaj.reducers :as collaj.reducers]
              [com.ben-allred.collaj.enhancers :as collaj.enhancers]
              [reagent.core :as r]
              [com.ben-allred.code-review-bot.utils.maps :as maps :include-macros true]))

(defn page
    ([] nil)
    ([state [type page]]
        (case type
            :router/navigate page
            state)))

(def reducer
    (collaj.reducers/combine (maps/->map page)))

(def ^:private store (collaj/create-custom-store r/atom
                         reducer
                         (collaj.enhancers/with-log-middleware (comp js/console.log pr-str))))

(def get-state (:get-state store))

(def dispatch (:dispatch store))
