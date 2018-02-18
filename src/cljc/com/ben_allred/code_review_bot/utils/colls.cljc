(ns com.ben-allred.code-review-bot.utils.colls)

(defn swap [coll idx-1 idx-2]
    (if (vector? coll)
        (assoc coll
            idx-1 (get coll idx-2)
            idx-2 (get coll idx-1))
        (->> coll
            (map-indexed #(cond
                              (= %1 idx-1) (nth coll idx-2)
                              (= %1 idx-2) (nth coll idx-1)
                              :else %2)))))

(defn exclude [coll idx]
    (cond-> coll
        :always (->>
                    (map-indexed vector)
                    (remove (comp (partial = idx) first))
                    (map second))
        (vector? coll) (vec)))
