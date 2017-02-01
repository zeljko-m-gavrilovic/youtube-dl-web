(ns youtube-dl-web.tracks
  (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse))
  (:require [clojure.java [jdbc :as j]]))
(def mysql-db
  {:subprotocol          "mysql"
   :subname              "//localhost:3306/youtube_converter"
   :user                 "root"
   :password             "root!"
   :zeroDateTimeBehavior "convertToNull"}
  )

(def h2-db {:classname   "org.h2.Driver"
            :subprotocol "h2:file"
            :subname     "youtube-dl-web;MODE=MYSQL;INIT=runscript from 'classpath:db.sql'"
            :user        "sa"
            :password    ""})

(def db h2-db)

(def now
  (str (java.sql.Timestamp. (System/currentTimeMillis))))

(defn all [tnx]
  (j/query tnx ["select * from tracks order by id desc"]))

(defn get-by-id [id tnx]
  (first (j/query tnx ["select * from tracks where id=?" id])))

(defn create [params tnx]
  (
    (def result (first (j/insert! tnx :tracks params)))
    ;;(def id (":scope_identity()" result))
    (def id (first (vals result)))
    id)
  )

(defn update-track [id params tnx]
  (j/update! tnx :tracks params ["id=?" id]))

(defn delete [id tnx]
  (j/delete! tnx :tracks ["id=?" id]))

(defn download [id tnx] (
                          let [
                               track (get-by-id id tnx)
                               url (:url track)
                               convert_to_mp3 (:convert_to_mp3 track)
                               request (new YoutubeDLRequest url)
                               ]
                          (if convert_to_mp3
                            (do (. request setOption "audio-format" "mp3")
                                (. request setOption "extract-audio")))
                          (def response (. YoutubeDL execute request))
                          (update-track id {:status "downloaded" :downloaded_at now :track_duration (. response getElapsedTime)} tnx)
                          response
                          ))
