;; All jangosmtp api function throws ExceptionInfo whenever there are errors in jangosmtp server. The exception info's data can be accessed by using clojure.core/ex-data
;;
;;

(ns clj-jangosmtp.core
  (:require [clj-http.client :as client]
            [clojure.data.xml :refer [parse-str]]
            [ribol.core :refer [manage on raise]]
            [clojure.tools.trace]))


(declare success? transactional-email-template)


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


(defmacro jangosmtp-request [req-fn succ-fn]
  `(try
     (~succ-fn (-> ~req-fn
                   :body
                   parse-str
                   :content
                   first))
     (catch Exception ~'e
       (raise {:jangosmtp-exception true 
               :data (-> ~'e
                         ex-data
                         :object)}))))


(defn check-bounce [j e]
  (jangosmtp-request
   (post j "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params {:EmailAddress e}})
   #(success? %)))


(defn- success? [s]
  (= (re-find #"^0\nSUCCESS" s) "0\nSUCCESS" ))




(defn delete-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (jangosmtp-request
     (client/post "https://api.jangomail.com/api.asmx/DeleteBounce" {:form-params d})
     #(success? %))))


(defn get-bounce-list-all 
  ([cnf] (get-bounce-list-all cnf ""))
  ([cnf since]
     (jangosmtp-request
      (client/post "https://api.jangomail.com/api.asmx/GetBounceListAll" {:form-params (assoc cnf :Since since)})
      (fn [s]
        (if (success? s)
          (-> (clojure.string/split s #"\n")
              (subvec 2))
          [])))))



(defn send-transactional-email [cnf te]
  (let [d (merge transactional-email-template cnf)]
    (jangosmtp-request
     (client/post "https://api.jangomail.com/api.asmx/SendTransactionalEmail" {:form-params d})
      (fn [s]
        (if (success? s)
          (-> (clojure.string/split s #"\n")
              (subvec 2)
              first)
          nil)))))
  

(def ^:private transactional-email-template 
  {:FromEmail ""
   :FromName ""
   :ToEmailAddress ""
   :Subject ""
   :MessagePlain ""
   :MessageHTML ""
   :Options ""})

