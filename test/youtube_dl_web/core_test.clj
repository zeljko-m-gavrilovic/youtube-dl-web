(ns youtube-dl-web.core-test

  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-dl-web.core :refer :all]
            [youtube-dl-web.tracks :as tracks]))

(def oneSongUrl "https://www.youtube.com/watch?v=K6uZ0nyWxnc")

(deftest test-index
  (testing "index route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      ;(is (= (:body response) "Hello World")
      )))

(deftest test-about 
  (testing "about route"
    (let [response (app (mock/request :get "/about"))]
      (is (= (:status response) 200))
      )))


(deftest test-new 
  (testing "new route"
    (let [response (app (mock/request :post "/persist-track" {:url oneSongUrl :note "for unit testing purpose"}))]
      (is (= (:status response) 302))
      )))

(deftest test-invalid
  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))     

;; (deftest test-download-one-song
;;   (testing "downloading one song"
;;     (let [response (app (mock/request :get "/download/16"))]
;;       (is (= (:status response) 200)))))     
;; (deftest test-download-unit
;;   (testing "downloading-unit:
;;     (let [track (tracks/download 16)]
;;     (track))))
;;
;; (let [track (tracks/download 16)]
;;     (track))
;; (run-tests)
;; (let [track (tracks/download 16)]((println (.getOut track))) (println (.getErr track)))
