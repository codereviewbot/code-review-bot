(defproject com.ben-allred/code-review-bot "0.1.0-SNAPSHOT"
    :description "Application simulator"
    :license {:name "Eclipse Public License"
              :url  "http://www.eclipse.org/legal/epl-v10.html"}
    :main com.ben-allred.code-review-bot.core
    :aot [com.ben-allred.code-review-bot.core]
    :min-lein-version "2.6.1"

    :dependencies [[clj-http "3.7.0"]
                   [com.ben-allred/collaj "0.4.0"]
                   [compojure "1.6.0"]
                   [environ "1.1.0"]
                   [http-kit "2.1.18"]
                   [log4j "1.2.16" :exclusions [[javax.mail/mail :extension "jar"]
                                                [javax.jms/jms :classifier "*"]
                                                com.sun.jdmk/jmxtools
                                                com.sun.jmx/jmxri]]
                   [org.clojure/clojure "1.9.0"]
                   [org.clojure/clojurescript "1.9.946"]
                   [org.clojure/core.async "0.3.465"]
                   [org.clojure/data.json "0.2.6"]
                   [org.clojure/test.check "0.9.0"]
                   [org.clojure/tools.logging "0.4.0"]
                   [org.clojure/tools.nrepl "0.2.12"]
                   [ring/ring-devel "1.6.3"]
                   [ring/ring-core "1.3.2"]
                   [ring/ring-defaults "0.2.1"]
                   [ring/ring-json "0.3.1"]]
    :source-paths ["src/clj"]
    :test-paths ["test/clj"]
    :profiles {:dev {:main com.ben-allred.code-review-bot.core/-dev
                     :source-paths  ["src/clj"]
                     :clean-targets ^{:protect false} [:target-path]}})
