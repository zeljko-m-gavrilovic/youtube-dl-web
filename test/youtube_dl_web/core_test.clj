(ns youtube-dl-web.core-test

  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-dl-web.core :as core]
            [youtube-dl-web.tracks :as tracks]))

(def one-song-url "https://www.youtube.com/watch?v=K6uZ0nyWxnc")

(def playlist-url "https://www.youtube.com/playlist?list=PLEy7dHChv8e21yQuAyc_cqUSDi95LG9sr")

(defn persist-track-request [url download? convert?]
  (let [response (core/app (mock/request :post "/persist-track" {:url url :note "for unit testing purpose" :download download? :convert_to_mp3 convert?}))]
    (is (= (:status response) 302))))

(defn download-track-request [url]
  (let [track (tracks/get-by-url url tracks/db)
        response (core/app (mock/request :get (str "/download/" (:id track))))]
    (is (= (:status response) 302))))

(defn delete-persisted-track [url] 
  (let [track (tracks/get-by-url url tracks/db)]
    (is (not (nil? track)))
    (tracks/delete (:id track))))

(defn check-persisted-track [url status] 
  (let [track (tracks/get-by-url url tracks/db)]
    (is (not (nil? track)))
    (is (= (:status track) status))
    (is (pos? (:track_duration track)))))

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

(deftest persist-one-song-no-download-and-no-convert
  (testing "persist one song, don't download and don't convert to mp3"
    (persist-track-request one-song-url 0 0)
    (check-persisted-track one-song-url nil)
    (delete-persisted-track one-song-url)))

(deftest persist-one-song-no-download-and-no-convert-then-download
  (testing "persist one song, don't download and don't convert to mp3 and then download it"
    (persist-track-request one-song-url 0 0)
    (check-persisted-track one-song-url nil)
    (download-track-request one-song-url)
    (check-persisted-track one-song-url "downloaded")
    (delete-persisted-track one-song-url)))

(deftest persist-one-song-no-download-and-convert
  (testing "persist one song, don't download and mark to convert to mp3"
    (persist-track-request one-song-url 0 1)
    (check-persisted-track one-song-url nil)
    (delete-persisted-track one-song-url)))

(deftest persist-one-song-download-and-convert
  (testing "persist one song marked to download and convert to mp3"
    (persist-track-request one-song-url 1 1)
    (check-persisted-track one-song-url "downloaded")
    (delete-persisted-track one-song-url)))

(deftest persist-one-song-download-no-conversion
  (testing "persist one song marked to download without conversion"
    (persist-track-request one-song-url 1 0)
    (check-persisted-track one-song-url "downloaded")
    (delete-persisted-track one-song-url)))
