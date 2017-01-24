(ns youtube-converter.tracks
 (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse))
 (:require [clojure.java [jdbc :as j]]))

(def mysql-db {:subprotocol "mysql"
 :subname "//localhost:3306/youtube_converter"
 :user "root"
 :password "root!"
 :zeroDateTimeBehavior "convertToNull"})

(def now
    (str (java.sql.Timestamp. (System/currentTimeMillis))))

(defn all []
 (j/query mysql-db ["select * from tracks order by id desc"]))

(defn get-by-id [id]
 (first (j/query mysql-db ["select * from tracks where id=?" id])))

(defn create [params]
    (
    (def result (first (j/insert! mysql-db :tracks params)))
    (def id (:generated_key result))
    id)
 )

(defn update [id params]
 (j/update! mysql-db :tracks params ["id=?" id]))

(defn delete [id]
 (j/delete! mysql-db :tracks ["id=?" id]))

(defn download [id] ( 
    let [
        track (get-by-id id)
        url (:url track)
        convert_to_mp3 (:convert_to_mp3 track)
        request (new YoutubeDLRequest url) 
    ]
    (if convert_to_mp3
        (do (. request setOption "audio-format" "mp3")
        (. request setOption "extract-audio")))
    (def response (. YoutubeDL execute request))
    (update id {:status "downloaded" :downloaded_at now :download_duration (. response getElapsedTime)})
     response
))

(comment
(def oneSongUrl "https://www.youtube.com/watch?v=K6uZ0nyWxnc")
(def playlistUrl "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")
(def id (create {:url playlistUrl :title "testname" :note "testdescription"}))
(println "id" id)
(def response (download id))
(println dtr)
(println (. dtr getExitCode))
(println (. dtr getOut))
(println (. dtr getErr))
(println (. dtr getDirectory))
(println (. dtr getElapsedTime))
)
;; (def videoInfo  (. YoutubeDL getVideoInfo url))
;; (println (. videoInfo fulltitle))
;; (println (. videoInfo duration))
;; (println (. videoInfo description))
;; (println (. videoInfo thumbnail))
;; (println (. videoInfo title))
