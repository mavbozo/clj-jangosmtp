;; All jangosmtp api function throws ExceptionInfo whenever there are errors in jangosmtp server. The exception info's data can be accessed by using clojure.core/ex-data
;;
;;

(ns clj-jangosmtp.core
  (:require [clj-http.client :as client]
            [clojure.data.xml :refer [parse-str]]
            [ribol.core :refer [manage on raise]]
            [clojure.tools.trace]))


(declare success? jangosmtp-request transactional-email-template)


(defprotocol HttpClient
  (post [service url req]))


(defrecord JangoSmtpApi [username password]
  HttpClient
  (post [service url req]
    (client/post url (update-in req 
                                [:form-params]
                                merge
                                {:Username username
                                 :Password password}))))


(defn jangosmtp-api [username password]
  (->JangoSmtpApi username password))


(defn check-bounce [j e]
  (jangosmtp-request
   {:req-fn #(post j "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params {:EmailAddress e}})
    :succ-fn success?}))


(defn delete-bounce [j e]
  (jangosmtp-request
   {:req-fn #(post j "https://api.jangomail.com/api.asmx/DeleteBounce" {:form-params {:EmailAddress e}})
    :succ-fn success?}))


(defn get-bounce-list-all 
  ([j] (get-bounce-list-all j ""))
  ([j since]
     (jangosmtp-request
      {:req-fn #(post j "https://api.jangomail.com/api.asmx/GetBounceListAll" {:form-params {:Since since}})
       :succ-fn (fn [s]
                  (if (success? s)
                    (-> (clojure.string/split s #"\n")
                        (subvec 2))
                    []))})))


(defn send-transactional-email [j te]
  (jangosmtp-request
   {:req-fn #(post j "https://api.jangomail.com/api.asmx/SendTransactionalEmail" {:form-params te})
    :succ-fn (fn [s]
                 (if (success? s)
                   (-> (clojure.string/split s #"\n")
                       (subvec 2)
                       first)
                   nil))}))


(def ^:private transactional-email-template 
  {:FromEmail ""
   :FromName ""
   :ToEmailAddress ""
   :Subject ""
   :MessagePlain ""
   :MessageHTML ""
   :Options ""})


(defn- success? [s]
  (= (re-find #"^0\nSUCCESS" s) "0\nSUCCESS" ))


(defn jangosmtp-request [{:keys [req-fn succ-fn]}]
  (try
    (succ-fn (->  (req-fn)
                   :body
                   parse-str
                   :content
                   first))
    (catch Exception e
      (raise {:jangosmtp-exception true 
              :data (-> e
                        ex-data
                        :object)}))))
