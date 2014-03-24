(ns clj-jangosmtp.core
  (:require [clj-http.client :as client]
            [clojure.data.xml :refer [parse-str]]))


(declare success? jangosmtp-request)


(defn check-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (jangosmtp-request
     (client/post "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params d})
     #(success? %)
     (fn [s] s))))


(defn get-bounce-list-all 
  ([cnf] (get-bounce-list-all cnf ""))
  ([cnf since]
     (jangosmtp-request
      (client/post "https://api.jangomail.com/api.asmx/GetBounceListAll" {:form-params (assoc cnf :Since since)})
      (fn [s]
        (if (success? s)
          (-> (clojure.string/split s #"\n")
              (subvec 2))
          []))
      (fn [s] s))))
             

(defn success? [s]
  (= (re-find #"^0\nSUCCESS" s) "0\nSUCCESS" ))



(defn delete-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (client/post "https://api.jangomail.com/api.asmx/DeleteBounce" {:form-params d})))

(dorun (map #(delete-bounce cnf %) result))



(defn bounce? [cnf e]
  (re-find #"^0\nSUCCESS" (check-bounce cnf e)))

(def te {:FromEmail ""
         :FromName ""
         :ToEmailAddress ""
         :Subject ""
         :MessagePlain ""
         :MessageHTML ""
         :Options ""})


(defn send-transactional-email [cnf te]
  (let [d (merge te cnf)]
    (client/post "https://api.jangomail.com/api.asmx/SendTransactionalEmail" {:form-params d})))

(defn do-via-thread [cnf m]
  (fn [] 
    (send-transactional-email cnf m)))
          


(defmacro jangosmtp-request [req-fn succ-fn ex-fn]
  `(try
     (~succ-fn (-> ~req-fn
                   :body
                   parse-str
                   :content
                   first))
     (catch Exception ~'e
       (-> ~'e
           ex-data
           :object
           :body
           (~ex-fn)))))
