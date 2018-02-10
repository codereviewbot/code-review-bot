(ns com.ben-allred.code-review-bot.utils.colors
    (:require [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.preds :as preds]))

(def ^:private colors
    (let [m (-> [:black :red :green :yellow :blue :magenta :cyan :white]
                (zipmap (map (partial + 30) (range))))]
        (fn [color]
            (get m color (:white m)))))

(def ^:private attributes
    (let [m (-> [:normal :bright :dim :italic :underline nil nil :invert]
                (zipmap (range))
                (dissoc nil))]
        (fn [attribute]
            (get m attribute (:normal m)))))

(declare colorfully)

(def ^:private default-schema
    {:hash        {:color :white :attribute :dim}
     :fn          {:color :white :attribute :italic}
     :bracket     {:color :white}
     :keyword     {:color :magenta}
     :symbol      {:color :yellow}
     :typed-type  {:color :cyan}
     :typed-value {:color :cyan :attribute :bright}
     :regex       {:color :green}
     :pr-str      {:color :green}
     :number      {:color :cyan}
     :boolean     {:color :blue :attribute :bright}
     :nil         {:color :red}
     :default     {:color :white :attribute :italic}})

(defn ^:private with-style
    ([message {:keys [color attribute trim?]}]
     (let [msg (if (string? message) message (pr-str message))]
         (format (str "\u001b[%d;%dm%s" (when-not trim? " ")) (attributes attribute) (colors color) msg))))

(defn ^:private surround
    ([begin end schema value style]
     (surround begin end colorfully schema value style))
    ([begin end mapper schema value style]
     (conj (into [[begin style]] (mapcat (partial mapper schema) value))
         [end style])))

(defn ^:private colorize* [f _ value css]
    [[(f value) css]])

(defn ^:private colorize-re* [re schema value & styles]
    (->> value
        (pr-str)
        (re-matches re)
        (rest)
        (interleave styles)
        (partition 2)
        (map reverse)
        (apply conj (colorize* str schema "#" (:hash schema)))))

(def ^:private colorize-fn
    (partial colorize* (constantly "#[fn]")))

(def ^:private colorize-map
    (partial surround "{" "}" (fn [schema [k v]] (into (colorfully schema k) (colorfully schema v)))))

(def ^:private colorize-set
    (partial surround "#{" "}"))

(def ^:private colorize-list
    (partial surround "(" ")"))

(def ^:private colorize-vector
    (partial surround "[" "]"))

(def ^:private colorize-typed-pr-str
    (partial colorize-re* #"#([a-z]+) (.+)"))

(def ^:private colorize-regex
    (partial colorize-re* #"#(.+)"))

(def ^:private colorize-symbol
    (partial colorize* (partial str "'")))

(def ^:private colorize-pr-str
    (partial colorize* pr-str))

(def ^:private colorize-basic
    (partial colorize* str))

(def ^:private pred-colorize-mappings
    [#{fn?} [colorize-fn [:fn]]
     #{map?} [colorize-map [:bracket]]
     #{set?} [colorize-set [:bracket]]
     #{list? seq?} [colorize-list [:bracket]]
     #{vector?} [colorize-vector [:bracket]]
     #{symbol?} [colorize-symbol [:symbol]]
     #{uuid? inst?} [colorize-typed-pr-str [:typed-type :typed-value]]
     #{preds/regexp?} [colorize-regex [:regex]]
     #{keyword?} [colorize-basic [:keyword]]
     #{number?} [colorize-basic [:number]]
     #{boolean?} [colorize-basic [:boolean]]
     #{string?} [colorize-pr-str [:pr-str]]
     #{nil?} [colorize-pr-str [:nil]]
     #{some?} [colorize-pr-str [:default]]])

(defn ^:private colorfully [schema value]
    (loop [[[preds [f style-keys]] & more] (partition 2 pred-colorize-mappings)]
        (cond
            ((apply preds/or? preds) value) (apply f schema value (map schema style-keys))
            (seq more) (recur more)
            :else (throw (ex-info (str "Cannot colorize: " (pr-str value)) value)))))

(defn ^:private space-n-style [styles]
    (loop [result "" [[message style] & more] styles]
        (let [[[next] :as nx] more
              style (assoc style :trim? (or (nil? nx)
                                            (contains? #{nil "{" "#{" "[" "(" "#"} message)
                                            (contains? #{"}" "]" ")"} next)))
              next-result (str result (with-style message style))]
            (if (empty? more)
                next-result
                (recur next-result more)))))

(defn colorize
    ([value] (colorize default-schema value))
    ([schema value]
     (->> value
         (colorfully schema)
         (space-n-style)
         (string/trim))))
