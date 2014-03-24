(defproject clj-jangosmtp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/mavbozo/clj-jangosmtp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.9.1"]
                 [org.clojure/data.xml "0.0.7"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [org.clojure/test.check "0.5.7"]]
                   :source-paths ["dev"]}}
  :repl-options {:init-ns user})


