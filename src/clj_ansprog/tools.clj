(ns clj-ansprog.tools
  (:require [clj-ansprog.core :as asp]
            [clj-ansprog.clingo-solver :as clingo]))


(defn prefix-term
  "prefix every top-levle term in the program with the given prefix as the first paramter "
  [[a & rest ] n]
  (vec (concat [a] [n] rest)))

(defn nest-terms
  "nest a list of answer set with the naswer set number"
  [terms prefix]
  (map #(prefix-term % prefix) terms))

(defn rename-term
  [[ _ & rest] newname]
  (vec (concat [newname] rest)))

(defn rename-terms
  [terms newname]
  (map #(rename-term % newname) terms))

(defn nest-solutions
  "Tales a solution, "
  [anssets]
  (apply concat (map-indexed #(nest-terms (asp/all-terms %2) %1)  anssets)))

(defn parse-input
  [input]
  (-> input
      (clojure.java.io/reader )
      (line-seq)
      (clingo/lineseq-anssets)
      (asp/->MemSolution)))

(defn terms->prog
  [terms]
  (apply str
         (map #(str (asp/term->str %) ".\n") terms)))

(defn nest-cmd
  [sln]
  (->  (asp/anssets sln)
       (nest-solutions)))



