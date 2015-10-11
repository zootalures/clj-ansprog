(ns clj-ansprog.core
  (:require [clojure.java.io :as io]))

(defprotocol AspSolver
  (solve [solver p]))

(defprotocol Program
  (prog-as-lines [prog]))

(defprotocol AnswerSet
  (all-terms [asset]))

(defrecord InMemAnswerSet [terms]
  AnswerSet
  (all-terms [_]
    terms))

(defrecord StringProg [lines]
  Program
  (prog-as-lines [_]
    lines))

(defn input->program
  "Construct an answer set program from an input stream, file or byte array"
  [instream]
  (->StringProg (line-seq (io/reader instream))))

(defn string->program
  "Create a program from a string"
  [prog]
  (input->program (.getBytes prog)))

(defn combine-programs
  "Combine multiple programs into one"
  [progs]
  (reify Program
    (prog-as-lines [_]
      (reduce concat (map prog-as-lines progs)))))