(ns clj-jangosmtp.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clj-jangosmtp.core :refer [HttpClient check-bounce delete-bounce get-bounce-list-all send-transactional-email]]))



(defrecord StubJangoSmtpApi [response]
  HttpClient
  (post [service url req]
    response))
  

(defn stub-jangosmtp-api [resp]
  (->StubJangoSmtpApi resp))


(def jango-success-str "0\nSUCCESS")


(defn response-generator
  [s]
  {:body
   (str "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://api.jangomail.com/\">" s "</string>") })



(defspec check-bounce-accepts-success-and-not-found
  100
  (prop/for-all [b gen/boolean]
                (let [s (if b jango-success-str "3\nNot Found")
                      j (stub-jangosmtp-api (response-generator s))]
                  (= b
                     (check-bounce j "")))))


(defspec delete-bounce-accepts-success
  100
  (prop/for-all [b gen/boolean]
                (let [j (stub-jangosmtp-api (response-generator jango-success-str))]
                  (delete-bounce j ""))))

                  
(defspec get-bounce-list-all-accepts-success
  100
  (prop/for-all [emails (gen/vector (gen/such-that (complement clojure.string/blank?) gen/string-alpha-numeric))]
                (let [s (clojure.string/join "\n" (concat [jango-success-str] emails))
                      j (stub-jangosmtp-api (response-generator s))]
                  (= emails
                     (get-bounce-list-all j "")))))


(defspec send-transactional-email-accepts-success
  100
  (prop/for-all [trans-id (gen/such-that (complement clojure.string/blank?) gen/string-alpha-numeric)]
                (let [s (str jango-success-str "\n" trans-id)
                      j (stub-jangosmtp-api (response-generator s))]
                  (= trans-id
                     (send-transactional-email j {})))))
