(ns youtube-converter.tracks-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-converter.core :refer :all]
            [youtube-converter.tracks :as tracks]
            [clojure.java.jdbc :as jdbc]))

(def oneSongUrl "https://www.youtube.com/watch?v=K6uZ0nyWxnc")
(def playlistUrl "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")
;; (def id (create {:url oneSongUrl :title "testname" :note "testdescription"}))
;;     (println "id" id)
;;     (def response (download id))
;;     (println dtr)
;;     (println (. dtr getExitCode))
;;     (println (. dtr getOut))
;;     (println (. dtr getErr))
;; (println (. dtr getDirectory))

;; (use-fixtures
;;   :once
;;   same as above)
(declare ^:dynamic tx)
(use-fixtures
  :each
  (fn [f]
    (jdbc/with-db-transaction
      [transaction tracks/db]
      (jdbc/db-set-rollback-only! transaction)
      (binding [tx transaction] (f)))))



(deftest when-persist-track-has-id
  (testing "persistence of one track"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id)))
      )))

(deftest when-get-by-id-track-is-found
  (testing "finding the track by its id"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)
          track (tracks/get-by-id id tx)]
      (is (not (nil? id)))
      (is (not (nil? (:id track))))
      )))

(deftest when-delete-by-id-track-is-gone
  (testing "deleting the track by its id"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id)))
      (tracks/delete id tx)
      (is (nil? (tracks/get-by-id id tx)))
      )))

(deftest when-downloaded-status-and-duration-are-populated
  (testing "downloading the track"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)
          response (tracks/download id tx)
          track (tracks/get-by-id id tx)
          ]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? (:status track))))
      (is (= (:status track) "downloaded"))
      (def track2 track)
      (is (pos? (:track_duration track)))
      )))
;; (println (. videoInfo fulltitle))
;; (println (. videoInfo duration))
;; (println (. videoInfo description))
;; (println (. videoInfo thumbnail))
;; (println (. videoInfo title))
