(ns clj-ansprog.core
  (:require [clojure.java.io :as io]
            [clojure.core.async :as async :refer [chan go <! <!! >! close!]]))

(defprotocol AspSolver
  (solve [solver p]
    [solver p opts]))

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


(defn- seq-up-to
  "return a sequence which contains all elements up to the first element matching fn "
  [inseq fn ]
  (if-let [non-emptyseq (seq inseq)]
    (if (fn (first non-emptyseq))
      []
      (lazy-seq (cons (first non-emptyseq) (seq-up-to (rest non-emptyseq) fn ))))
    []))

(defn- stage-match-fn
  [stage]
  (fn [line]
    (if-let [[_ s] (re-find #"%stage\s+(\d+)\s*" (str line))]
      (> (Integer/parseInt s) stage))))

(defn filter-stage
  [program stage]
  (->StringProg
    (seq-up-to (prog-as-lines program)  (stage-match-fn stage))))


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


(defn term->str
  [t]
  (cond (sequential? t)
        (str (name (first t))
             (if (seq (rest t))
               (str "(" (clojure.string/join "," (map term->str (rest t))) ")")
               ""))
        :default t
        ))
