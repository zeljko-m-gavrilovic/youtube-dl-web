(ns youtube-dl-web.core-test

  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-dl-web.core :as core]
            [youtube-dl-web.tracks :as tracks]))

(def one-song-url "https://www.youtube.com/watch?v=K6uZ0nyWxnc")

(def playlist-url "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")

(deftest index-page-reachable
  (testing "index page reached"
    (let [response (core/app (mock/request :get "/"))]
      (is (= (:status response) 200)))))

(deftest about-page-is-reachable
  (testing "about page is reachable"
    (let [response (core/app (mock/request :get "/about"))]
      (is (= (:status response) 200)))))

(deftest for-invalid-request-you-will-get-404-response
  (testing "for an invalid request you will get page not found response"
    (let [response (core/app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest track-can-be-persisted
  (testing "persisting a track"
    (let [response (core/app (mock/request :post "/persist-track" {:url one-song-url :note "for unit testing purpose"}))]
      (is (= (:status response) 302)))
    (let [track (tracks/get-by-url one-song-url tracks/db)]
      (is (not (nil? track)))
      (tracks/delete (:id track) tracks/db))))

(defn download-url [url]
  (let [response (core/app (mock/request :post "/persist-track" {:url url :note "for unit testing purpose" :convert_to_mp3 1 :download 1}))]
    (is (= (:status response) 302)))
  (let [downloaded (tracks/get-by-url url tracks/db)]
    (is (not (nil? downloaded)))
    (is (= (:status downloaded) "downloaded"))
    (is (pos? (:track_duration downloaded)))
    (tracks/delete (:id downloaded) tracks/db)))

(deftest possible-to-download-one-song
  (testing "download one song"
    (download-url one-song-url)))

(deftest possible-to-download-playlist
  (testing "download all songs from playlist"
    (download-url playlist-url)))
