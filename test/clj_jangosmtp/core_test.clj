(ns clj-jangosmtp.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clj-jangosmtp.core :refer [check-bounce]]))


(defn http-response-mock 
  ([]
     {:body
      (str "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://api.jangomail.com/\">" "0\nSUCCESS" "</string>") })
  ([p]
     (http-response-mock))
  ([a b]
     (http-response-mock))
  ([a b & more]
     
     (http-response-mock)))


;;"0\nSUCCESS"
;;"3\nNot Found"

(defspec check-bounce-accepts-success-and-not-found
  1000
  (prop/for-all [s gen/int]
                (true? 
                 (with-redefs [clj-http.client/post http-response-mock]
                   (check-bounce {} "")))))
  
  


