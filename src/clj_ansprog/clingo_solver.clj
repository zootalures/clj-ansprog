(ns clj-ansprog.clingo-solver
  (:require [me.raynes.conch :as conch]
            [instaparse.core :as insta]
            [clojure.core.async :refer [chan go <! <!! >! close!]])
  (:require [clj-ansprog.core :as asp]
            [clj-ansprog.core :as api]))

(conch/programs clingo)


(def term-grammar-def
  "S = ATOM|TERM;
  P = LITERAL|ATOM|TERM;
  LITERAL = #'-?\\d+(\\.\\d+)?';
  ATOM = #'-?[a-zA-z][\\w_]*';
  TERM = #'-?[a-zA-z][\\w_]*\\s*\\(\\s*' (P ( <#'\\s*,\\s*'>  P )* )? <#'\\s*\\)'>;")

(def handler
  {:S       identity
   :P       identity
   :LITERAL (fn [v] (read-string v))
   :ATOM    (fn [name]
              [(keyword (clojure.string/trim name))])
   :TERM    (fn [name & args]
              (apply (partial conj [(keyword (clojure.string/replace name #"\s*\(\s*$" ""))]) args))
   })

(def term-grammar
  (insta/parser term-grammar-def))

(defn parse-term
  [term]
  "Parses a term into it's clojure form"
  (->>
    term
    (clojure.string/trim)
    (term-grammar)
    (insta/transform handler)))

(defn parse-ansset
  "Parse a single Answer set line into an answer set object"
  [asset]
  (->>
    (clojure.string/split asset #"\s+")
    (filter not-empty)
    (map parse-term)
    (set)
    (asp/->InMemAnswerSet)))


(defn lineseq-anssets
  "Parses clingo output as a line sequence into a lazy sequence of answer sets"
  [lines]
  (lazy-seq
    (if-let [l (first lines)]
      (do
        ;(println "handling " l )
        (if-let [r (next lines)]
          (if (re-find #"Answer: (\d+)" l)
            (cons (parse-ansset (first r)) (lineseq-anssets (rest r)))
            (lineseq-anssets r)))))))

(defn- clingo-solve
  "Invokes clingo and parses results - returning a channel
    answer set objects"
  [prog]
  (conch/let-programs
    [clingo "clingo"]
    (let [output (chan)
          result (clingo "-n" "0" {:throw   false
                                   :seq     true
                                   :in      (api/prog-as-lines prog)
                                   :verbose true})]
      (go
        (try
          (doseq [ansset (lineseq-anssets (:stdout result))]
            (>! output ansset))
          (catch Exception e
            (println "Failed handling answer set " e)
            (throw e))
          (finally
            (close! output))))
      {:anssets-channel output
       :proc    (:proc result)})))


(defrecord ClingoSolver [opts]
  asp/AspSolver
  (solve [_ prog]
    (api/map->AsyncSolution (clingo-solve prog))))

(defn create-clingo-solver
  "Create a clingo solver"
  [opts]
  (->ClingoSolver opts))


