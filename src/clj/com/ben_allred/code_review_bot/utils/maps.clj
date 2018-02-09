(ns com.ben-allred.code-review-bot.utils.maps)

(defn update-maybe [m k f & f-args]
    (if (get m k)
        (apply update m k f f-args)
        m))

(defn map-kv [key-fn val-fn m]
    (->> m
        (map (fn [[k v]] [(key-fn k) (val-fn v)]))
        (into {})))

(defn map-keys [key-fn m]
    (map-kv key-fn identity m))

(defn map-vals [val-fn m]
    (map-kv identity val-fn m))
