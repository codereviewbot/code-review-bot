(ns com.ben-allred.code-review-bot.ui.services.store.actions
    (:require [com.ben-allred.code-review-bot.ui.utils.http :as http]))

(def request-user-details
    (fn [[dispatch]]
        (dispatch [:user/request])
        (http/get "/auth/details"
            (comp dispatch (partial conj [:user/succeed]))
            (comp dispatch (partial conj [:user/fail])))))
