(ns youtube-dl-web.core
  (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse))
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as resp]
            ;; [youtube-dl-web.views :as views]
            [youtube-dl-web.tracks :as tracks]
            [selmer.parser :refer [render-file]]
            [selmer.filters :refer [add-filter!]]
            [markdown.core :as md]
            [clojure.java.io :as io]
            )
  )


(defn persist-track [request]
  (let [
        url (get-in request [:params :url])
        note (get-in request [:params :note])
        directory (get-in request [:params :directory])
        convert_to_mp3 (get-in request [:params :convert_to_mp3])
        download_flag (get-in request [:params :download])
        videoInfo (. YoutubeDL getVideoInfo url)
        fulltitle (. videoInfo fulltitle)
        track_duration (. videoInfo duration)
        thumbnail (. videoInfo thumbnail)
        ]
    (def id (tracks/create
              {:url       url :note note :directory directory
               :title     fulltitle :track_duration track_duration
               :thumbnail thumbnail :convert_to_mp3 convert_to_mp3} tracks/db))
    (if download_flag (tracks/download id tracks/db))
    (resp/redirect "/")))

(add-filter! :markdown md/md-to-html-string)

(defroutes app-routes
           (GET "/" [] (render-file "templates/tracks.html" {:tracks (tracks/all tracks/db)}))

           ;; (GET "/about" [] (render-file "templates/about.html" {}))
           (GET "/about" [] (render-file "templates/about.html" {:about (slurp (io/resource "README.md"))}))

           (GET "/download/:id" [id] (
                                       do (tracks/download id tracks/db)
                                          (resp/redirect "/")))

           (GET "/track-form" []
             (render-file "templates/track-form.html" {}))

           (POST "/persist-track" [] persist-track)

           (GET "/delete/:id" [id]
             (do (tracks/delete id tracks/db)
                 (resp/redirect "/")))

           (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> (routes app-routes)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
