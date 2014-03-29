(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer (javadoc)]
   [clojure.pprint :refer (pprint)]
   [clojure.reflect :refer (reflect)]
   [clojure.repl :refer (apropos dir doc find-doc pst source)]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [clj-jangosmtp.core :refer [check-bounce jangosmtp-api]]))
   


(defn tests []
  (test/run-tests 'clj-jangosmtp.core-test))
  

(defn reset []
  (refresh :after 'user/tests))


(comment 
  (fn []
;; handy keystroke for emacs user
    (defun cider-repl-reset ()
      (interactive)
      (save-some-buffers)
      (with-current-buffer (cider-current-repl-buffer)
        (goto-char (point-max))
        (insert "(user/reset)")
        (cider-repl-return)))

    (global-set-key (kbd "C-c r") 'cider-repl-reset)

    )
)
