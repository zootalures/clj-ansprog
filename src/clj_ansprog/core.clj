(ns clj-ansprog.core
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async :refer [chan go <! <!! >! close!]]))

(defprotocol AspSolver
  (solve [solver p]))

(defprotocol Solution
  (anssets [_]))


(defn- sets-from-chan
  [channel]
  (if-let [val (<!! channel ) ]
    (lazy-seq (cons val (sets-from-chan channel)))))

(defrecord AsyncSolution [anssets-channel]
  Solution
  (anssets [_]
    (sets-from-chan anssets-channel)))

(defrecord MemSolution [seq]
  Solution
  (anssets [_]
    seq))

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

(defn term-arity
  "Return the arity of a term"
  [t]
  (when (vector? t)
    (- (count t) 1)))

(defn term-name
  "return the name of a term "
  [t]
  (first t))

(defn term-filter
  "Create a term filter predicate base on the name and arity of a terms"
  ([name]
   (term-filter name 0))
  ([name arity]
   (fn [term]
     (and
       (vector? term)
       (= name (first term))
       (= arity (term-arity term))))))


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