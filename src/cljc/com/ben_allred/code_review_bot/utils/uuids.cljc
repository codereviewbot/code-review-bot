(ns com.ben-allred.code-review-bot.utils.uuids
    #?(:clj
       (:import [java.util UUID])))

(defn ->uuid [v]
    (if (uuid? v)
        v
        #?(:clj  (UUID/fromString v)
           :cljs (uuid v))))

(defn random []
    #?(:clj (UUID/randomUUID)
       :cljs (throw (ex-info "Not implemented" {}))))
