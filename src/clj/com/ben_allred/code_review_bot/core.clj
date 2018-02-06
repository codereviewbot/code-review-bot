(ns com.ben-allred.code-review-bot.core
    (:gen-class)
    (:use compojure.core org.httpkit.server)
    (:require
        [compojure.handler :refer [site]]
        [ring.middleware.reload :refer [wrap-reload]]
        [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
        [clojure.tools.nrepl.server :as nrepl]
        [com.ben-allred.code-review-bot.routes.git-hook :as git-hooks]
        [com.ben-allred.code-review-bot.utils.env :as env]))

(defroutes ^:private base
    (context "/api" [] git-hooks/webhooks)
    (GET "/health" [] {:status 200 :body {:a :ok}}))

(def ^:private app
    (-> #'base
        (wrap-json-response)
        (wrap-json-body {:keywords? true :bigdecimals? true})
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
