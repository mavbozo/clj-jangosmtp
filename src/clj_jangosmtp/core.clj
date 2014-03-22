(ns clj-jangosmtp.core
  (:require [clj-http.client :as client]))


(def cnf {:Password "" :Username "avicenna"})

(def t {:Password "", :Username "avicenna", :Since ""})

(defn get-bounce-list-all [cnf]
  (client/post "https://api.jangomail.com/api.asmx/GetBounceListAll" {:form-params (assoc cnf :Since "")}))

(def r (get-bounce-list-all cnf))

(def b (:body r))

(defn success? [s]
  (re-find #"^0\nSUCCESS" s))


(def result-str (-> (re-find #"<string xmlns=\"http\:\/\/api\.jangomail\.com/\">(?s)(.*)</string>" b)
                    (nth 1)))

(def result (-> result-str
                (clojure.string/split #"\n")
                (subvec 2)))


(defn delete-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)]
    (client/post "https://api.jangomail.com/api.asmx/DeleteBounce" {:form-params d})))

(dorun (map #(delete-bounce cnf %) result))

(defn check-bounce [cnf e]
  (let [d (assoc cnf :EmailAddress e)
        r (client/post "https://api.jangomail.com/api.asmx/CheckBounce" {:form-params d})
        b (:body r)]
    (-> (re-find #"<string xmlns=\"http\:\/\/api\.jangomail\.com/\">(?s)(.*)</string>" b)
        (nth 1))))


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
          
