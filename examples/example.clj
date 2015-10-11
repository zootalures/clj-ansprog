(ns clj-ansprog.example
    (:require
      [clj-ansprog.core :as asp]
      [clj-ansprog.clingo-solver :as clingo]))

(def solver (clingo/create-clingo-solver {}))

(def prog (asp/string->program "a :- not b. \n b :- not a."))

(map asp/all-terms  (asp/solve solver prog))