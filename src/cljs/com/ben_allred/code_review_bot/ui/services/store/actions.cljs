(ns com.ben-allred.code-review-bot.ui.services.store.actions
    (:require [com.ben-allred.code-review-bot.ui.utils.http :as http]))

(defn ^:private cb [dispatch type]
    (comp dispatch (partial conj [type])))

(def request-user-details
    (fn [[dispatch]]
        (dispatch [:user/request])
        (http/get "/auth/details"
            (cb dispatch :user/succeed)
            (cb dispatch :user/fail))))

(def request-repos
    (fn [[dispatch]]
        (dispatch [:repos/request])
        (http/get "/api/repos"
            (cb dispatch :repos/succeed)
            (cb dispatch :repos/fail))))
