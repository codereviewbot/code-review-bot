(ns com.ben-allred.code-review-bot.ui.services.navigation
    (:require [bidi.bidi :as bidi]
              [clojure.string :as string]
              [com.ben-allred.code-review-bot.utils.keywords :as keywords]
              [pushy.core :as pushy]
              [com.ben-allred.code-review-bot.ui.services.store.core :as store]))

(defn ^:private namify [[k v]]
    [k (str (keywords/safe-name v))])

(defn ^:private parse-qp [s]
    (->> (string/split s #"&")
        (map #(string/split % #"="))
        (filter (comp seq first))
        (reduce (fn [qp [k v]] (assoc qp (keyword k) (or v true))) {})))

(defn ^:private join-qp [qp]
    (->> qp
        (map namify)
        (map (partial string/join "="))
        (string/join "&")))

(def ^:private routes
    ["/"
     [["" :home]
      ["login" :login]
      ["logout" :logout]
      ["repos"
       [["" :repos]
        [["/" [#"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}" :repo-id]] :repo]]]
      [true :not-found]]])

(defn match-route [path]
    (let [qp (parse-qp (second (string/split path #"\?")))]
        (cond->
            (bidi/match-route routes path)
            (seq qp) (assoc :query-params qp))))

(defn path-for
    ([page] (path-for page nil))
    ([page {:keys [query-params] :as params}]
                (let [qp (join-qp query-params)]
                    (cond-> (apply bidi/path-for routes page (mapcat namify params))
                        (seq qp) (str "?" qp)))))

(defonce history
    (let [history (pushy/pushy (comp store/dispatch (partial conj [:router/navigate])) match-route)]
        (pushy/start! history)
        history))

(defn reload! []
    (.reload (.-location js/window)))

(defn navigate!
    ([page] (navigate! page nil))
    ([page params]
        (pushy/set-token! history (path-for page params))))

(defn nav-and-replace!
    ([page] (nav-and-replace! page nil))
    ([page params]
        (pushy/replace-token! history (path-for page params))))

(defn link [{:keys [page params]} & children]
    (into [:a {:href "#" :on-click (fn [_] (navigate! page params))}] children))
