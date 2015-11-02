(defproject clj-ansprog "0.1.0-SNAPSHOT"
  :description "Clojure answer set programming tools"
  :url "http://zootalures.github.io/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [me.raynes/conch "0.8.0"]
                 [instaparse "1.4.1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :profiles {:dev {:dependencies [[midje "1.7.0"]]}}
  :jvm-opts ["-Xverify:none"]

  )
