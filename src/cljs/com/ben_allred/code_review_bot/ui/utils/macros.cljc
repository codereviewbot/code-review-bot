(ns com.ben-allred.code-review-bot.ui.utils.macros)

(defmacro after [ms & body]
    `(.setTimeout js/window (fn [] ~@body) ~ms))
