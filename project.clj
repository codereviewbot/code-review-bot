(defproject com.ben-allred/code-review-bot "0.1.0-SNAPSHOT"
    :description "Application simulator"
    :license {:name "Eclipse Public License"
              :url  "http://www.eclipse.org/legal/epl-v10.html"}
    :main com.ben-allred.code-review-bot.core
    :aot [com.ben-allred.code-review-bot.core]
    :min-lein-version "2.6.1"

    :dependencies [[clj-http "3.7.0"]
                   [cljs-http "0.1.43"]
                   [com.ben-allred/collaj "0.4.0"]
                   [com.novemberain/monger "3.1.0" :exclusions [com.google.guava/guava]]
                   [compojure "1.6.0"]
                   [environ "1.1.0"]
                   [http-kit "2.1.18"]
                   [log4j "1.2.16" :exclusions [[javax.mail/mail :extension "jar"]
                                                [javax.jms/jms :classifier "*"]
                                                com.sun.jdmk/jmxtools
                                                com.sun.jmx/jmxri]]
                   [metosin/jsonista "0.1.1"]
                   [org.clojure/clojure "1.9.0"]
                   [org.clojure/clojurescript "1.9.946"]
                   [org.clojure/clojurescript "1.9.946"]
                   [org.clojure/core.async "0.3.465"]
                   [org.clojure/data.json "0.2.6"]
                   [org.clojure/test.check "0.9.0"]
                   [org.clojure/tools.logging "0.4.0"]
                   [org.clojure/tools.nrepl "0.2.12"]
                   [reagent "0.7.0"]
                   [ring/ring-core "1.3.2"]
                   [ring/ring-defaults "0.2.1"]
                   [ring/ring-devel "1.6.3"]
                   [ring/ring-json "0.3.1"]]

    :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
              [lein-cooper "1.2.2"]
              [lein-figwheel "0.5.14"]
              [lein-sassy "1.0.8"]]

    :jar-name "code-review-bot.jar"
    :uberjar-name "code-review-bot-standalone.jar"
    :source-paths ["src/clj" "src/cljs"]
    :test-paths ["test/clj" "test/cljs"]

    :cljsbuild {:builds [{:id           "dev"
                          :source-paths ["src/cljs"]
                          :figwheel     {:on-jsload "com.ben-allred.code-review-bot.ui.core/on-js-reload"}
                          :compiler     {:main                 com.ben-allred.code-review-bot.ui.core
                                         :asset-path           "js/compiled/out"
                                         :output-to            "resources/public/js/compiled/code_review_bot.js"
                                         :output-dir           "resources/public/js/compiled/out"
                                         :source-map-timestamp true
                                         :preloads             [devtools.preload]}}
                         {:id           "min"
                          :source-paths ["src/cljs"]
                          :compiler     {:output-to     "resources/public/js/compiled/code_review_bot.js"
                                         :main          com.ben-allred.code-review-bot.ui.core
                                         :optimizations :advanced
                                         :pretty-print  false}}]}

    :figwheel {:css-dirs   ["resources/public/css"]
               :nrepl-port 7888}
    :sass {:src "src/scss"
           :dst "resources/public/css/"}

    :cooper {"cljs"   ["lein" "figwheel"]
             "sass"   ["lein" "sass" "watch"]
             "server" ["lein" "run"]}

    :profiles {:dev {:dependencies  [[binaryage/devtools "0.9.4"]
                                     [figwheel-sidecar "0.5.14"]
                                     [com.cemerick/piggieback "0.2.2"]]
                     :main          com.ben-allred.code-review-bot.core/-dev
                     :source-paths  ["src/clj" "src/cljs" "dev"]
                     :plugins [[cider/cider-nrepl "0.12.0"]]
                     :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                       :target-path]
                     :repl-options  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
