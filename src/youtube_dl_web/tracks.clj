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

(def out "
[youtube] K6uZ0nyWxnc: Downloading webpage
[youtube] K6uZ0nyWxnc: Downloading video info webpage
[youtube] K6uZ0nyWxnc: Extracting video information
[youtube] K6uZ0nyWxnc: Downloading MPD manifest
[download] Destination:     downloads/NA/NA-SluÄajni Prolaznik - Godine.mp4

[download]   0.0% of 8.08MiB at  2.43KiB/s ETA 56:38
[download]   0.0% of 8.08MiB at  7.30KiB/s ETA 18:53
[download]   0.1% of 8.08MiB at 17.03KiB/s ETA 08:05
[download]   0.2% of 8.08MiB at 36.48KiB/s ETA 03:46
[download]   0.4% of 8.08MiB at 53.68KiB/s ETA 02:33
[download]   0.8% of 8.08MiB at 59.31KiB/s ETA 02:18
[download]   1.5% of 8.08MiB at 72.46KiB/s ETA 01:52
[download]   2.7% of 8.08MiB at 73.40KiB/s ETA 01:49
[download]   3.6% of 8.08MiB at 78.20KiB/s ETA 01:42
[download]   4.7% of 8.08MiB at 91.11KiB/s ETA 01:26
[download]   6.9% of 8.08MiB at 125.97KiB/s ETA 01:01
[download]  11.4% of 8.08MiB at 189.75KiB/s ETA 00:38
[download]  20.2% of 8.08MiB at 295.49KiB/s ETA 00:22
[download]  32.8% of 8.08MiB at 431.63KiB/s ETA 00:12
[download]  52.9% of 8.08MiB at 610.62KiB/s ETA 00:06
[download]  75.7% of 8.08MiB at 747.57KiB/s ETA 00:02
[download]  94.5% of 8.08MiB at 829.80KiB/s ETA 00:00
[download] 100.0% of 8.08MiB at 850.52KiB/s ETA 00:00
[download] 100% of 8.08MiB in 00:09
[ffmpeg] Destination: downloads/NA/NA-SluÄajni Prolaznik - Godine.mp3
Deleting original file downloads/NA/NA-SluÄajni Prolaznik - Godine.mp4 (pass -k to keep)"
)

(defn playlist? [response] 
  (cs/starts-with? (.getOut response) "[youtube:playlist]"))

(defn extract-file-path [response convert-to-mp3] 
  (let[splitted (cs/split-lines (.getOut response))
       destination-marker (if convert-to-mp3 "[ffmpeg] Destination" "[download] Destination:")
       filtered (filter (fn[e] (cs/starts-with? e destination-marker)) splitted)
       destination-line (first filtered)
       destination-value (nth (cs/split destination-line #":") 1)
       file-path (cs/trim destination-value)]
       (if (playlist? response)
         (let [last-index-of-slash (cs/last-index-of file-path "/")
               directory (subs file-path 0 last-index-of-slash)] 
           directory)
         file-path)))


(defn extract-title [track response] 
  (if (playlist? response) 
    (let [splitted (cs/split-lines (.getOut response))
          playlist-row (nth splitted 2)
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
    (.setOption request "o" "downloads/%(playlist)s/%(playlist_index)s-%(title)s.%(ext)s")
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

(defn persist-track [params]
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
                   db)]
    (if download_flag (download id db))))

(defn file-exist [track]
    (.exists (io/file (:file_path track))))

(defn files-exist [tracks]
  (map (fn [track] (assoc track :file_exist (file-exist track))) tracks))
