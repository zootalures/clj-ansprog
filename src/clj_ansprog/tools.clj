(ns clj-ansprog.tools
  (:require [clj-ansprog.core :as asp]
            [clj-ansprog.clingo-solver :as clingo]))


(defn- prefix-term
  "prefix every top-levle term in the program with the given prefix as the first paramter "
  [[a & rest ] n]
  (apply (partial vec a n) rest))

(defn nest-terms
  "nest a list of answer set with the naswer set number"
  [terms prefix]
  (map #(prefix-term % prefix) terms))


(defn nest-solutions-cmd
  "Tales a sequence of "
  [solution]
  (map-indexed #(nest-terms ) nest-terms ()))

(defn- main [cmd & args]
  (case cmd
    "nest" (nest-solutions (clingo/read-solution *in*))
    true
  ))