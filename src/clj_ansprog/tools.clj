(ns clj-ansprog.tools
  (:require [clj-ansprog.core :as asp]
            [clj-ansprog.clingo-solver :as clingo]
            [clojure.java.io :as io]
            [clj-ansprog.rotations :as rot])
  (:import (clj_ansprog.core Solution))
  (:gen-class))


(defn- prefix-term
  "prefix every top-levle term in the program with the given prefix as the first paramter "
  [[a & rest ] n]
  (vec (concat [a] [n] rest)))

(defn nest-terms
  "nest a list of answer set with the naswer set number"
  [terms prefix]
  (map #(prefix-term % prefix) terms))


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


(defn write-terms-as-prog
  [ terms ^java.io.Writer out ]
  (doseq [t terms]
    (.write out (asp/term->str t))
    (.write out ".\n")
    (.flush out)))


(defn
  nest-cmd
  [sln]
  (->  (asp/anssets sln)
       (nest-solutions)))

(defn
  generate-parts-cmd
  [sln]
  (as-> (asp/anssets sln) $
        (map asp/all-terms $)
        (map rot/gen-rotations $)
        (into #{} $)
        (map first $)
        (map asp/->InMemAnswerSet $)
        (nest-solutions $)
        ))


(defn -main
  [& args]
  (case (first args)
    "nest"
    (do (-> (parse-input *in*)
            (nest-cmd )
            (write-terms-as-prog  *out*))
        0)
    "generateparts" ;generate distinct parts irrspective of roatation
    (do (-> (parse-input *in*)
            (generate-parts-cmd )
            (write-terms-as-prog  *out*))
        0)
    "generatepartsedn" ;generate distinct parts irrspective of roatation
    (do (-> (parse-input *in*)
            (generate-parts-cmd)
            (prn-str))
        0)
    (do
      (println "cmd <args> ")
      1)))