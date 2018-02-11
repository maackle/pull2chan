(defproject pull2chan "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]
                 [com.cognitect/transit-cljs "0.8.243"]
                 [funcool/struct "1.2.0"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.13"]
            [lein-doo "0.1.7"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {:main pull2chan.core
                                   :output-to "target/dev.js"
                                   :output-dir "target/dev"
                                   :target :nodejs
                                   :optimizations :none
                                   }}
                       {:id "devserver"
                        :source-paths ["server" "test" "src"]
                        :figwheel true
                        :compiler {
                                   :main devserver.core
                                   :output-to "target/devserver.js"
                                   :output-dir "target/devserver"
                                   :target :nodejs
                                   :optimizations :none
                                   }}
                       {:id "test"
                        :source-paths ["test" "src"]
                        :figwheel true
                        :compiler {:main pull2chan.tests
                                   :output-to "target/js/test.js"
                                   :output-dir "target/js/test"
                                   :target :nodejs
                                   :optimizations :none
                                   }}
                       {:id "prod"
                        :source-paths ["src"]
                        :compiler {:main pull2chan.core
                                   :output-to "target/prod.js"
                                   :output-dir "target/prod"
                                   :target :nodejs
                                   :optimizations :advanced}}]
  }

  :figwheel {}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
             :front [:dev {:figwheel {:server-port 3669}}]
             :back [:dev {:figwheel {:server-port 3779}}]})
