(ns com.ben-allred.code-review-bot.ui.services.events)

(def code->key
    {13 :enter
     27 :esc})

(def key->code
    (let [code->key' (seq code->key)]
        (zipmap (map key code->key') (map val code->key'))))
