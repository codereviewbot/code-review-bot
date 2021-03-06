(ns com.ben-allred.code-review-bot.ui.services.events
    (:require [com.ben-allred.code-review-bot.utils.keywords :as keywords]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(def code->key
    {13 :enter
     27 :esc})

(def key->code
    (let [code->key' (seq code->key)]
        (zipmap (map key code->key') (map val code->key'))))

(defn ->key-code [event]
    (let [code-ns (cond->> []
                      (.-shiftKey event) (cons "shift")
                      (.-metaKey event) (cons "meta")
                      (.-ctrlKey event) (cons "ctrl")
                      (.-altKey event) (cons "alt")
                      :always (string/join "."))
          code    (key->code (.-keyCode event))]
        (if (and code (seq code-ns))
            (keywords/join "/" [code-ns code])
            code)))
