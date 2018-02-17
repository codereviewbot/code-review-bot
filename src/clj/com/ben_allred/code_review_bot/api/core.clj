(ns com.ben-allred.code-review-bot.api.core
    (:gen-class)
    (:use compojure.core org.httpkit.server)
    (:require [compojure.handler :refer [site]]
              [compojure.route :as route]
              [ring.middleware.reload :refer [wrap-reload]]
              [ring.middleware.json :as mw.json]
              [clojure.tools.nrepl.server :as nrepl]
              [com.ben-allred.code-review-bot.api.auth.core :as auth]
              [com.ben-allred.code-review-bot.api.routes.hooks :as hooks]
              [com.ben-allred.code-review-bot.api.routes.repos :as repos]
              [com.ben-allred.code-review-bot.api.services.middleware :as middleware]
              [com.ben-allred.code-review-bot.api.utils.env :as env]
              [ring.util.response :as response]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defroutes ^:private base
    (context "/api" []
        (context "/hooks" [] hooks/webhooks)
        (context "/repos" [] repos/repos))
    (context "/auth" [] auth/auth)
    (GET "/health" [] {:status 200 :body {:a :ok}})
    (route/resources "/")
    (GET "/*" [] (response/resource-response "index.html" {:root "public"})))

(def ^:private app
    (-> #'base
        (middleware/log-response)
        (middleware/decode-jwt)
        (middleware/content-type)
        (site)))

(defn ^:private run [app]
    (run-server #'app {:port env/server-port})
    (println "Server is listening on port" env/server-port))

(defn -main [& args]
    (run app))

(defn -dev [& args]
    (println "Server is running with #'wrap-reload")
    (run (wrap-reload #'app))
    (nrepl/start-server :port env/nrepl-port)
    (println "REPL is listening on port" env/nrepl-port))