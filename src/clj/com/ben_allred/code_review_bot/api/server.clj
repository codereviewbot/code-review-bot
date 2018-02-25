(ns com.ben-allred.code-review-bot.api.server
    (:gen-class)
    (:use compojure.core org.httpkit.server)
    (:require [compojure.handler :refer [site]]
              [compojure.route :as route]
              [ring.middleware.reload :refer [wrap-reload]]
              [ring.middleware.json :as mw.json]
              [clojure.tools.nrepl.server :as nrepl]
              [com.ben-allred.code-review-bot.api.routes.auth :as auth]
              [com.ben-allred.code-review-bot.api.routes.hooks :as hooks]
              [com.ben-allred.code-review-bot.api.routes.configs :as configs]
              [com.ben-allred.code-review-bot.api.services.middleware :as middleware]
              [ring.util.response :as response]
              [com.ben-allred.code-review-bot.services.env :as env]
              [com.ben-allred.code-review-bot.utils.logging :as log]))

(defroutes ^:private base
    (context "/api" []
        (context "/hooks" [] hooks/webhooks)
        (context "/configs" [] configs/configs))
    (context "/auth" [] auth/auth)
    (GET "/health" [] {:status 200 :body {:a :ok}})
    (route/resources "/")
    (GET "/*" [] (response/resource-response "index.html" {:root "public"}))
    (ANY "/*" [] {:status 404}))

(def ^:private app
    (-> #'base
        (middleware/log-response)
        (middleware/with-auth-user)
        (middleware/content-type)
        (site)))

(def ^:private server-port
    (if-let [port (env/get :port)]
        (Integer/parseInt (str port))
        3000))

(defn ^:private run [app]
    (run-server #'app {:port server-port})
    (println "Server is listening on port" server-port))

(defn -main [& args]
    (run app))

(defn -dev [& args]
    (println "Server is running with #'wrap-reload")
    (run (wrap-reload #'app))
    (nrepl/start-server :port 7000)
    (println "REPL is listening on port" 7000))
