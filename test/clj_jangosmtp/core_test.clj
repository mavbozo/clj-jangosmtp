(ns clj-jangosmtp.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clj-jangosmtp.core :refer :all]))


(def jango-success-str "0\nSUCCESS")


(defn http-response-mock-generator [s]
  (fn 
    ([x & more]
       {:body
        (str "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://api.jangomail.com/\">" s "</string>") })))


(defspec check-bounce-accepts-success-and-not-found
  100
  (prop/for-all [b gen/boolean]
                (let [s (if b jango-success-str "3\nNot Found")]
                  (= b
                     (with-redefs [clj-http.client/post (http-response-mock-generator s)]
                       (check-bounce {} ""))))))


(defspec delete-bounce-accepts-success
  100
  (prop/for-all [b gen/boolean]
                (with-redefs [clj-http.client/post (http-response-mock-generator jango-success-str)]
                  (delete-bounce {} ""))))


(defspec get-bounce-list-all-accepts-success
  100
  (prop/for-all [emails (gen/vector (gen/such-that (complement clojure.string/blank?) gen/string-alpha-numeric))]
                (let [s (clojure.string/join "\n" (concat [jango-success-str] emails))]
                  (= emails
                     (with-redefs [clj-http.client/post (http-response-mock-generator s)]
                       (get-bounce-list-all {} ""))))))


(defspec send-transactional-email-accepts-success
  100
  (prop/for-all [trans-id (gen/such-that (complement clojure.string/blank?) gen/string-alpha-numeric)]
                (let [s (str jango-success-str "\n" trans-id)]
                  (= trans-id
                     (with-redefs [clj-http.client/post (http-response-mock-generator s)]
                       (send-transactional-email {} ""))))))
                 
                


