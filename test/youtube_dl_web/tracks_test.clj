(ns youtube-dl-web.tracks-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-dl-web.tracks :as tracks]
            [clojure.java.jdbc :as jdbc]))

(def oneSongUrl "https://www.youtube.com/watch?v=K6uZ0nyWxnc")
(def playlistUrl "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")

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
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id)))
      )))

(deftest track-can-be-found-by-id
  (testing "track can be found by id"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)
          track (tracks/get-by-id id tx)]
      (is (not (nil? id)))
      )))

(deftest track-can-be-delete-by-id
  (testing "track can be deleted by the tracks id"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)]
      (is (not (nil? id)))
      (tracks/delete id tx)
      (is (nil? (tracks/get-by-id id tx)))
      )))

(deftest track-can-be-downloaded-and-status-and-duration-are-populated
  (testing "downloaded track has the adequate status and duration"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tx)
          response (tracks/download id tx)
          track (tracks/get-by-id id tx)
          ]
      (is (not (nil? id)))
      (is (not (nil? response)))
      (is (not (nil? (:status track))))
      (is (= (:status track) "downloaded"))
      (is (pos? (:track_duration track)))
      )))
