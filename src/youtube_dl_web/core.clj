(ns youtube-dl-web.core
  (:require [compojure.core :as cc]
            [compojure.route :as cr]
            [ring.middleware.defaults :as rmd]
            [ring.util.response :as resp]
            [youtube-dl-web.tracks :as tracks]
            [selmer.parser :as sp]
            [selmer.filters :as sf]
            [markdown.core :as md]
            [clojure.java.io :as io]))

(sf/add-filter! :markdown md/md-to-html-string)
(sf/add-filter! :format-seconds-to-minutes tracks/format-seconds-to-minutes)
(sf/add-filter! :format-miliseconds-to-minutes tracks/format-miliseconds-to-minutes)
(sf/add-filter! :format-time tracks/format-time)


(cc/defroutes app-routes
    (cc/GET "/" [] 
      (sp/render-file "templates/tracks.html" {:tracks (tracks/files-exist (tracks/all tracks/db))}))

    (cc/GET "/about" [] 
      (sp/render-file "templates/about.html" {:about (slurp (io/resource "README.md"))}))

    (cc/GET "/download/:id" [id] (do 
      (tracks/download id tracks/db)
      (resp/redirect "/")))

    (cc/GET "/track-form" []
      (sp/render-file "templates/track-form.html" {}))

    (cc/POST "/persist-track" request 
        (tracks/persist-track (:params request) tracks/db)
        (resp/redirect "/"))

    (cc/GET "/delete/:id" [id]
        (tracks/delete id tracks/db)
        (resp/redirect "/"))

    (cr/not-found "<h1>Page not found</h1>"))

(def app
  (-> (cc/routes app-routes)
      (rmd/wrap-defaults (assoc-in rmd/site-defaults [:security :anti-forgery] false))))
