(ns youtube-dl-web.views
  (:require [hiccup.core :refer (html)]
            [hiccup.form :as f]
            [hiccup.page :as p]
            [youtube-dl-web.tracks :as tracks]))

(defn layout [title & content]
  (p/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     (p/include-css "css/bootstrap.min.css"
                    "css/bootstrap-theme.min.css")
     (p/include-js "js/jquery-1.10.2.min.js"
                   "js/bootstrap.min.js"
                   )
     [:title title]
     ]
    [:body
     [:nav {:class "navbar navbar-inverse navbar-fixed-top"}
      [:div {:class "container"}
       [:div {:class "navbar-header"}
        [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse" :data-target "#navbar" :aria-expanded "false" :aria-controls "navbar"}]
        [:span :class "sr-only" "Toggle navigation"]
        [:span :class "icon-bar"]
        [:span :class "icon-bar"]
        [:span :class "icon-bar"]
        [:a {:class "navbar-brand" :href "#"} "Project name"]
        ]
       [:div {:id "navbar" :class "collapse navbar-collapse"}
        [:ul {:class "nav navbar-nav"}
         [:li {:class "active"} [:a {:href "#"} "Home"]]
         [:li [:a {:href "#about"} "About"]]
         [:li [:a {:href "#contact"} "Contact"]]
         ]
        ]
       ]
      ]

     [:div {:class "container"}
      [:div {:class "starter-template"}
       [:h1 "Bootstrap starter template"]
       [:p {:class "lead"} "Use this document as a way to quickly start any new project.<br> All you get is this text and a mostly barebones HTML document."]
       ]
      ]
     ]))

(defn main-page []
  (println "main-page function")
  (layout "My Blog"
          [:h1 "My Blog"]
          [:p "Welcome to my page"]))

; Track is a map corresponding to a record from the database
(defn track-row [track]

  (let [id (:id track)
        title (:title track)
        url (:url track)
        note (:note track)
        track_duration (:track_duration track)
        thumbnail (:thumbnail track)
        downloaded_at (:downloaded_at track)
        download_duration (:download_duration track)
        status (:status track)
        ]
    (html
      [:tr [:td title]
       [:td url]
       [:td note]
       [:td track_duration]
       [:td downloaded_at]
       [:td download_duration]
       [:td status]
       [:td [:img {:src thumbnail :width "50px" :height "50px"}]]
       [:td [:a {:href (str "/delete/" id)} "Delete"]
        [:a {:href (str "/download/" id)} "Download"]]]
      )
    ))

(defn tracks-table []
  (layout "My Tracks"
          [:h1 "Youtube converter"]
          [:h2 "All my tracks"]
          [:a {:href "/track-form"} "Add"]
          (html
            [:table {:border "1px solid"}
             [:tr [:th "Title"] [:th "Url"] [:th "Note"] [:th "Track duration"] [:th "Downloaded at"] [:th "Time to download"] [:th "Status"] [:th "Thumbnail"] [:th "Actions"]]
             (map #(track-row %) (tracks/all))
             ]
            )
          )
  )
;(map #(track %) (tracks/all))])))


(defn track-form []
  (html
    [:div.track
     [:form {:action "/persist-track" :method "post"}
      [:div.column-1
       [:input#url-input {:type "text" :name "url" :placeholder "Url"}]]
      [:div.column-2
       [:input#description-input {:type "text" :name "note" :placeholder "Note"}]]
      [:button.button.add {:type "submit"} "Add "]]
     [:div.clear-row]]))

;; (defn add-track-2 []
;;  (layout "My Blog - Add Post"
;;   (list
;;    [:h2 "Add Post"]
;;    (f/form-to [:track "/admin/create"]
;;     (f/label "name" "Name")
;;     (f/text-field "name") [:br]
;;     (f/label "url" "Url")
;;     (f/text-field "url") [:br]
;;     (f/label "description" "Description") [:br]
;;     (f/text-area {:rows 20} "description") [:br]
;;     (f/submit-button "Save")))))

;; (defn edit-track [id]
;;  (layout "My Blog - Edit Post"
;;   (list
;;    (let [track (tracks/get-by-id id)]
;;     [:h2 (str "Edit Track " id)]
;;     (f/form-to [:track "save"]
;;      (f/label "name" "Name")
;;      (f/text-field "name" (:name track)) [:br]
;;      (f/label "url" "Url")
;;      (f/text-field "url" (:url track)) [:br]
;;      (f/label "description" "Description") [:br]
;;      (f/text-area {:rows 20} "description" (:description track)) [:br]
;;      (f/submit-button "Save"))))))
