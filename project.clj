(defproject youtube-converter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"] 
                 [ring/ring-defaults "0.2.1"]
                 ;[ring/ring-jetty-adapter "1.6.0-beta6"]
                 ;[proto-repl "0.3.1"]
                 ;[enlive "1.1.6"]
                 [com.github.sapher/youtubedl-java "1.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [hiccup "1.0.5"]
                 [selmer "1.10.5"]
                 ]
   :repositories [["jitpack" "https://jitpack.io"]]
   :plugins [[lein-ring "0.10.0"]]
   :ring {:handler youtube-converter.core/app}
   :profiles
   {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
                                           
