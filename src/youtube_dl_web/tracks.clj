(ns youtube-dl-web.tracks
  (:require [clojure.java.jdbc :as j]
            [clojure.java.io :as io]
            [clojure.string :as cs])
  (:import (com.sapher.youtubedl YoutubeDLRequest YoutubeDL YoutubeDLResponse)))

(def mysql-db
  {:subprotocol          "mysql"
   :subname              "//localhost:3306/youtube_converter"
   :user                 "root"
   :password             "root!"
   :zeroDateTimeBehavior "convertToNull"})

(def h2-db
  {:classname   "org.h2.Driver"
   :subprotocol "h2:file"
   :subname     "youtube-dl-web;MODE=MYSQL;INIT=runscript from 'classpath:db.sql'"
   :user        "sa"
   :password    ""})

(def db h2-db)

(def download_folder "downloads")

(def now
  (str (java.sql.Timestamp. (System/currentTimeMillis))))

(defn all [tnx]
  (j/query tnx ["select * from tracks order by id desc"]))

(defn get-by-id [id tnx]
  (first (j/query tnx ["select * from tracks where id=?" id])))

(defn get-by-url [url tnx]
  (first (j/query tnx ["select * from tracks where url=?" url])))

(defn create [params tnx]
  (let [result (first (j/insert! tnx :tracks params))
        id (first (vals result))]
    id))

(defn update-track [id params tnx]
  (j/update! tnx :tracks params ["id=?" id]))

(defn delete [id tnx]
  (j/delete! tnx :tracks ["id=?" id]))

(defn format-seconds-to-minutes [seconds]
  (if (and (not (nil? seconds)) (> seconds 60))
    (str (quot seconds 60) "m:" (rem seconds 60) "s")
    (str seconds "s")))

(defn format-miliseconds-to-minutes [miliseconds]
  (if (and (not (nil? miliseconds)) (> miliseconds 1000))
    (str (quot miliseconds 1000) "s:" (rem miliseconds 1000) "ms")
    (str miliseconds "ms")))

(defn format-time [date]
  (if (not (nil? date))
    (.format (java.text.SimpleDateFormat. "dd.MM.yyyy HH:mm:ss") date)
    ""))

(defn playlist? [response] 
  (cs/starts-with? (.getOut response) "[youtube:playlist]"))


(defn extract-file-path-for-playlist [response]
  (let [splitted (cs/split-lines (.getOut response))
        destination-marker (if (.contains (.getOut response) "has already been downloaded") (str "[download] " download_folder) "[download] Destination:")
        filtered (filter (fn[e] (cs/starts-with? e destination-marker)) splitted)
        line-with-path (first filtered)
        splitted-line-with-path (cs/split line-with-path #" ")
        destination_path (first (filter (fn[e] (cs/starts-with? e download_folder)) splitted-line-with-path))
        last-index-of-slash (cs/last-index-of (cs/trim destination_path) "/")
        directory_path (subs (cs/trim destination_path) 0 last-index-of-slash)]
    directory_path))

(defn extract-file-path-for-one-song [response convert-to-mp3]
  (let [splitted (cs/split-lines (.getOut response))
       destination-marker (if convert-to-mp3 "[ffmpeg] Destination" "[download]")
       filtered (filter (fn[e] (cs/starts-with? e destination-marker)) splitted)
       line-with-path (first filtered)
       splitted-line-with-path (cs/split line-with-path #" ")
       destination_path (first (filter (fn[e] (cs/starts-with? e download_folder)) splitted-line-with-path))
       file-path (cs/trim destination_path)]
    file-path))

(defn extract-file-path [response convert-to-mp3]
  (if (playlist? response)
    (extract-file-path-for-playlist response)
    (extract-file-path-for-one-song response convert-to-mp3)))

(defn extract-title [track response] 
  (if (playlist? response)
    (let [splitted (cs/split-lines (.getOut response))
          playlist-row (first (filter
                         (fn[e] (cs/starts-with? e "[download] Finished downloading playlist:")) splitted))
          playlist-value (nth (cs/split playlist-row #":") 1)
          playlist-name (cs/trim playlist-value)]
          playlist-name)
    (:title track)))

(defn download [id tnx] 
  (let [track (get-by-id id tnx)
        url (:url track)
        convert_to_mp3 (:convert_to_mp3 track)
        request (YoutubeDLRequest. url)]
    (when convert_to_mp3
        (.setOption request "audio-format" "mp3")
        (.setOption request "extract-audio"))
    (.setOption request "o" (str download_folder "/%(playlist)s/%(playlist_index)s-%(title)s.%(ext)s"))
    (.setOption request "format" "mp4")
    (.setOption request "restrict-filenames")
    (let [response (YoutubeDL/execute request)
          playlist (playlist? response)
          file-path (extract-file-path response (:convert_to_mp3 track))]
        (update-track id {:status "downloaded"
                        :downloaded_at now
                        :download_duration (.getElapsedTime response)
                        :playlist playlist
                        :file_path file-path
                        :title (extract-title track response)}
                    tnx)
        response)))

(defn persist-track [params tnx]
  (let [url (:url params)
        note (:note params)
        convert_to_mp3 (:convert_to_mp3 params)
        download_flag (:download params)
        videoInfo (YoutubeDL/getVideoInfo url)
        fulltitle (.fulltitle videoInfo )
        track_duration (.duration videoInfo )
        thumbnail (.thumbnail videoInfo )
        id (create {:url            url
                    :note           note
                    :title          fulltitle
                    :track_duration track_duration
                    :thumbnail      thumbnail
                    :convert_to_mp3 convert_to_mp3}
                   tnx)]
    (if download_flag (download id tnx))
    id))

(defn file-exist [track]
  (if (not (nil? (:file_path track)))
    (.exists (io/file (:file_path track)))
    false))

(defn files-exist [tracks]
  (map (fn [track] (assoc track :file_exist (file-exist track))) tracks))
