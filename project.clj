(defproject youtube-dl-web "0.1.0-SNAPSHOT"
  :description "download and convert youtube videos and playlists"
  :url "https://github.com/zeljko-m-gavrilovic/youtube-dl-web"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"] 
                 [ring/ring-defaults "0.2.1"]
                 [com.github.sapher/youtubedl-java "1.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.6"]
                 [hiccup "1.0.5"]
                 [com.h2database/h2 "1.3.170"]
                 [selmer "1.10.5"]
                 [markdown-clj "0.9.94"]
                 ]
   :repositories [["jitpack" "https://jitpack.io"]]
   :plugins [[lein-ring "0.10.0"]]
   :ring {:handler youtube-dl-web.core/app}
   :profiles
   {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
