(ns youtube-dl-web.tracks-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-dl-web.tracks :as tracks]
            [clojure.java.jdbc :as jdbc]))

(def one-song-url "https://www.youtube.com/watch?v=K6uZ0nyWxnc")

(def playlist-url "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")

(declare ^:dynamic tx)

(use-fixtures
  :each
  (fn [f]
    (jdbc/with-db-transaction
      [transaction tracks/db]
      (jdbc/db-set-rollback-only! transaction)
      (binding [tx transaction] (f)))))

(deftest after-persisting-a-track-it-has-id
  (testing "persisting one track"
    (let [id (tracks/persist-track {:url one-song-url :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id))))))

(deftest track-can-be-found-by-id
  (testing "track can be found by id"
    (let [id (tracks/persist-track {:url one-song-url :title "testname" :note "testdescription"} tx)
          track (tracks/get-by-id id tx)]
      (is (not (nil? id))))))

(deftest track-can-be-delete-by-id
(testing "track can be deleted by the tracks id"
    (let [id (tracks/persist-track {:url one-song-url :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id)))
      (tracks/delete id tx)
      (is (nil? (tracks/get-by-id id tx))))))

(deftest after-downloading-one-song-no-conversion-status-and-duration-are-populated
(testing "downloaded track has the adequate status and duration"
    (let [id (tracks/persist-track {:url one-song-url :title "testname" :note "testdescription"} tx)
          response (tracks/download id tx)
          track (tracks/get-by-id id tx)]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? track)))
      (is (not (nil? (:status track))))
      (is (= (:status track) "downloaded"))
      (is (pos? (:track_duration track))))))

(deftest after-downloading-playlist-no-conversion-status-and-duration-are-populated
  (testing "downloaded playlist has the adequate status and duration"
    (let [id (tracks/persist-track {:url playlist-url :title "testname" :note "testdescription"} tx)
          response (tracks/download id tx)
          track (tracks/get-by-id id tx)]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? track)))
      (is (not (nil? (:status track))))
      (is (= (:status track) "downloaded"))
      (is (pos? (:track_duration track))))))

(deftest after-downloading-one-song-no-conversion-file-exists
  (testing "song track has the correct file path after it is downloaded"
    (let [id (tracks/persist-track {:url one-song-url :title "testname" :note "testdescription"} tx)
          response (tracks/download id tx)
          tracks (tracks/all tx)]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? tracks)))
      (is (not (empty? tracks)))
      (is (= (count tracks) 1))
      (is (tracks/file-exist (first tracks))))))

(deftest after-downloading-one-song-do-conversion-then-file-exists
  (testing "song track has the correct file path after it is downloaded"
    (let [id (tracks/persist-track {:url one-song-url :title "testname"
                                    :note "testdescription" :convert_to_mp3 1} tx)
          response (tracks/download id tx)
          tracks (tracks/all tx)]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? tracks)))
      (is (not (empty? tracks)))
      (is (= (count tracks) 1))
      (is (tracks/file-exist (first tracks))))))


(deftest after-downloading-playlist-track-has-the-correct-file-path
  (testing "playlist track has the correct file path"
    (let [id (tracks/persist-track {:url playlist-url :title "testname"
                                    :note "testdescription"} tx)
          response (tracks/download id tx)
          tracks (tracks/all tx)]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? tracks)))
      (is (not (empty? tracks)))
      (is (= (count tracks) 1))
      (is (tracks/file-exist (first tracks))))))