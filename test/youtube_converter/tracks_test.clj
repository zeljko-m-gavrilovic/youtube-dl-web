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
(declare ^:dynamic tnx) 
(use-fixtures
  :each
  (fn [f]
    (jdbc/with-db-transaction
      [transaction tracks/db]
        (jdbc/db-set-rollback-only! transaction)
      (binding [tnx transaction] (f)))))



(deftest test-pesrsisting-one-track
  (testing "persistence of one track"
    (let [id (tracks/create {:url oneSongUrl :title "testname" :note "testdescription"} tnx)]
    (is (not (nil? id)))
)))
;; (println (. videoInfo fulltitle))
;; (println (. videoInfo duration))
;; (println (. videoInfo description))
;; (println (. videoInfo thumbnail))
;; (println (. videoInfo title))
