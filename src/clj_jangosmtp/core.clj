;; All jangosmtp api function throws ExceptionInfo whenever there are errors in jangosmtp server. The exception info's data can be accessed by using clojure.core/ex-data
;;
;;

(ns clj-jangosmtp.core
  (:require [clj-http.client :refer [post]]
            [clojure.data.xml :refer [parse-str]]
            [ribol.core :refer [manage on raise]]
            [clojure.tools.trace]))



(defmacro spy-env []
  (let [ks (keys &env)]
    `(prn (zipmap '~ks [~@ks]))))


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





(declare success? jangosmtp-request transactional-email-template)


(comment
(defn ^:dynamic check-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (post "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params d}))))


(defn ^:dynamic check-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (jangosmtp-request
     (post "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params d})
     #(success? %))))


(defn get-bounce-list-all 
  ([cnf] (get-bounce-list-all cnf ""))
  ([cnf since]
     (jangosmtp-request
      (post "https://api.jangomail.com/api.asmx/GetBounceListAll" {:form-params (assoc cnf :Since since)}))
      (fn [s]
        (if (success? s)
          (-> (clojure.string/split s #"\n")
              (subvec 2))
          []))))




(defn delete-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (jangosmtp-request
     (post "https://api.jangomail.com/api.asmx/DeleteBounce" {:form-params d})
     #(success? %))))


(defn send-transactional-email [cnf te]
  (let [d (merge transactional-email-template cnf)]
    (jangosmtp-request
     (post "https://api.jangomail.com/api.asmx/SendTransactionalEmail" {:form-params d})
     #(success?))))
  

(def ^:private transactional-email-template 
  {:FromEmail ""
   :FromName ""
   :ToEmailAddress ""
   :Subject ""
   :MessagePlain ""
   :MessageHTML ""
   :Options ""})


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


(defn- success? [s]
  (= (re-find #"^0\nSUCCESS" s) "0\nSUCCESS" ))



(defn ^:dynamic foo []
  (with-redefs [post http-response-mock]
    (check-bounce {} "")))
