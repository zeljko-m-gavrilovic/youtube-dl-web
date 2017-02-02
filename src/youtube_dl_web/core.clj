(ns youtube-dl-web.core
  (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse))
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as resp]
            [youtube-dl-web.tracks :as tracks]
            [selmer.parser :refer [render-file]]
            [selmer.filters :refer [add-filter!]]
            [markdown.core :as md]
            [clojure.java.io :as io]
            )
  )

(add-filter! :markdown md/md-to-html-string)

(defroutes app-routes
    (GET "/" [] (render-file "templates/tracks.html" {:tracks (tracks/all tracks/db)}))

    (GET "/about" [] (render-file "templates/about.html" {:about (slurp (io/resource "README.md"))}))

    (GET "/download/:id" [id] (do 
        (tracks/download id tracks/db)
        (resp/redirect "/")
    ))

    (GET "/track-form" []
        (render-file "templates/track-form.html" {}))

    (POST "/persist-track" request 
        (tracks/persist-track (:params request))
        (resp/redirect "/")
    )

    (GET "/delete/:id" [id]
        (tracks/delete id tracks/db)
        (resp/redirect "/")
    )

    (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> (routes app-routes)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))
