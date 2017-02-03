(ns youtube-dl-web.tracks
    (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse))
    (:require [clojure.java.jdbc :as j]))

(def mysql-db
    {
        :subprotocol          "mysql"
        :subname              "//localhost:3306/youtube_converter"
        :user                 "root"
        :password             "root!"
        :zeroDateTimeBehavior "convertToNull"
    }
)

(def h2-db 
    {
        :classname   "org.h2.Driver"
        :subprotocol "h2:file"
        :subname     "youtube-dl-web;MODE=MYSQL;INIT=runscript from 'classpath:db.sql'"
        :user        "sa"
        :password    ""
    }
)

(def db h2-db)

(def now
  (str (java.sql.Timestamp. (System/currentTimeMillis))))

(defn all [tnx]
  (j/query tnx ["select * from tracks order by id desc"]))

(defn get-by-id [id tnx]
  (first (j/query tnx ["select * from tracks where id=?" id])))

(defn get-by-url [url tnx]
  (first (j/query tnx ["select * from tracks where url=?" url])))

(defn create [params tnx]
    (let [
        result (first (j/insert! tnx :tracks params))
        id (first (vals result))
        ]
    id)
)

(defn update-track [id params tnx]
  (j/update! tnx :tracks params ["id=?" id]))

(defn delete [id tnx]
  (j/delete! tnx :tracks ["id=?" id]))

(defn format-seconds-to-minutes [seconds] 
    (if (> seconds 60)
        (str (quot seconds 60) "m:" (rem seconds 60) "s")
        (str seconds "s")
    )
) 

(defn format-miliseconds-to-minutes [miliseconds] 
    (if (> miliseconds 1000)
        (str (quot miliseconds 1000) "s:" (rem miliseconds 1000) "ms")
        (str miliseconds "ms")
    )
) 

(defn format-time [date]
(.format (java.text.SimpleDateFormat. "dd.MM.yyyy HH:mm:ss") date))


(defn download [id tnx] (
    let [
        track (get-by-id id tnx)
        url (:url track)
        convert_to_mp3 (:convert_to_mp3 track)
        request (new YoutubeDLRequest url)
        ]
        (if convert_to_mp3
            (do (. request setOption "audio-format" "mp3")
                (. request setOption "extract-audio"))
        )
        (. request setOption "o" "downloads/%(playlist)s/%(playlist_index)s-%(title)s.%(ext)s")
        (. request setOption "format" "mp4")
        (let [
            response (. YoutubeDL execute request)
            ]
            (update-track   id {:status "downloaded" 
                                :downloaded_at now 
                                :download_duration (. response getElapsedTime)} 
                            tnx)
        response)
    )
)

(defn persist-track [params]
    (let [
        url (:url params)
        note (:note params)
        convert_to_mp3 (:convert_to_mp3 params)
        download_flag (:download params)
        videoInfo (. YoutubeDL getVideoInfo url)
        fulltitle (. videoInfo fulltitle)
        track_duration (. videoInfo duration)
        thumbnail (. videoInfo thumbnail)
        id (create
                {:url url 
                :note note 
                :title     fulltitle 
                :track_duration track_duration
                :thumbnail thumbnail 
                :convert_to_mp3 convert_to_mp3} 
            db)
        ]
        (if download_flag (download id db))
    )
)
