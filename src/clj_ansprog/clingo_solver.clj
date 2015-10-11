(ns clj-ansprog.clingo-solver
  (:require [me.raynes.conch :as conch]
            [instaparse.core :as insta])
  (:require [clj-ansprog.core :as asp]
            [clj-ansprog.core :as api]))

(conch/programs clingo)


(def term-grammar-def
  "S = LITERAL|ATOM|TERM;
  LITERAL = #'-?\\d+(\\.\\d+)?';
  ATOM = #'-?[a-zA-z][\\w_]*';
  TERM = #'-?[a-zA-z][\\w_]*\\s*\\(\\s*' (S ( <#'\\s*,\\s*'>  S )* )? <#'\\s*\\)'>;
  ")

(def handler
  {:S identity
   :LITERAL (fn [v] (read-string v))
   :ATOM (fn [name]
           (keyword (clojure.string/trim name)))
   :TERM (fn [name & args]
           (apply (partial conj [(keyword (clojure.string/replace name #"\s*\(\s*$" ""))]) args))
   })

(def term-grammar
  (insta/parser term-grammar-def))

(defn parse-term
  [term]
  "Parses a term into it's clojure form"
  (insta/transform handler(term-grammar  (clojure.string/trim  term))))

(defn parse-ansset
  "Parse a single Answer set line into an answer set object"
  [asset]
  (asp/->InMemAnswerSet
    (map parse-term (filter  not-empty (clojure.string/split asset #"\s+")))))


(defn- lineseq-anssets
  "Parses clingo output as a line sequence into a lazy sequence of answer sets"
  [lines]
  (lazy-seq
    (if-let [l (first lines)]
      (if-let [r (next lines)]
        (if (re-find #"Answer: (\d+)" l)
          (cons (parse-ansset (first r)) (lineseq-anssets (rest r)))
          (lineseq-anssets r))))))

(defn- clingo-solve
  "Invokes clingo and parses results - returning a lazy sequence of answer set objects"
  [prog]
  (conch/let-programs
    [clingo "clingo"]
    (lineseq-anssets (clingo "-n" "0" {:throw false
                                       :seq   true
                                       :in    (api/prog-as-lines prog)}))))

(defrecord ClingoSolver [opts]
  asp/AspSolver
  (solve [_ prog]
    (clingo-solve prog)))


(defn create-clingo-solver
  "Create a clingo solver"
  [opts]
  (->ClingoSolver opts))