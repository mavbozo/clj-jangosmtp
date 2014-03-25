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
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(def cnf {:Password "" :Username "avicenna"})

(def t {:Password "", :Username "avicenna", :Since ""})


(declare my-macro)


(defn my-plus [x y]
  (+ x y))

(defn my-plus-but-minus [x y]
  (- x y))


(defn my-plus-in-macro [x y]
  (my-macro (my-plus x y)))

(defmacro my-macro [my-fn]
  `~my-fn)


(comment 
  (with-redefs [my-plus my-plus-but-minus]
    (my-plus-in-macro 1 2))
)
