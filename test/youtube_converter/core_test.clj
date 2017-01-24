(ns youtube-converter.core-test

  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [youtube-converter.core :refer :all]
            [youtube-converter.tracks :as tracks]))

(deftest test-app

  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      ;(is (= (:body response) "Hello World")
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
;;   (testing "downloading-unit"
;;     (let [track (tracks/download 16)]
;;     (track))))
;;
;; (let [track (tracks/download 16)]
;;     (track))
;; (run-tests)
;; (let [track (tracks/download 16)]((println (.getOut track))) (println (.getErr track)))
