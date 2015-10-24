(ns clj-ansprog.clingo-solver-test
  (:require [midje.sweet :refer :all])
  (:require
            [clj-ansprog.core :as api]
            [clj-ansprog.clingo-solver :refer :all]))

(facts "about the answer set term parser"
       (fact "it should parse number literal params"
             (parse-term "a(1)") => [:a 1]
             (parse-term "a(1.1)") => [:a 1.1]
             (parse-term "a(-1)") => [:a -1]
             (parse-term "a(-1.0)") => [:a -1.0])

       (fact "it should parse atoms"
             (parse-term "a") => [:a]
             (parse-term " a ") => [:a]
             (parse-term "a_b") => [:a_b]
             (parse-term "-b") => [:-b])

       (fact "it should parse terms"
             (parse-term "a()") => [:a]
             (parse-term "a ()") => [:a]
             (parse-term "a_b()") => [:a_b]
             (parse-term "-a_b()") => [:-a_b]
             (parse-term "a(1)") => [:a 1]
             (parse-term "a( 1 )") => [:a 1]
             (parse-term "a(1,2,3)") => [:a 1 2 3]
             (parse-term "a( 1 , 2 , 3 )") => [:a 1 2 3]
             (parse-term "a(1,2,b(1.2))") => [:a 1 2 [:b 1.2]]
             (parse-term "a(a(a(a)))") => [:a [:a [:a [:a]]]]))

(defn just-anssets
  [sln]
  (map api/all-terms (api/anssets sln)))

(facts "about clingo solver"
      (fact "it should solve a single answer set prog"
            (let
              [engine (create-clingo-solver {})
               basic-prog (api/->StringProg ["a.","b."])]

              (just-anssets (api/solve engine basic-prog)) =>
              [#{[:a] [:b]}]))

      (fact "it should solve programs with multiple answer sets"
            (let
              [engine (create-clingo-solver {})
               basic-prog (api/->StringProg ["a :- not b.","b :- not a."])]

              (just-anssets (api/solve engine basic-prog)) =>
              [#{[:a]} #{[:b]}]))

      (fact "it should give an empty answer set for a program with no output  "
            (let
              [engine (create-clingo-solver {})
               basic-prog (api/->StringProg ["a :- -a."])]
              (just-anssets (api/solve engine basic-prog)) => [#{}]))

      (fact "it should give no  answer sets for a program with no satisfiable answers "
            (let
              [engine (create-clingo-solver {})
               basic-prog (api/->StringProg ["a. -a."])]
              (just-anssets (api/solve engine basic-prog)) => []))

       (fact "It should return answer sets for parameterised terms"
             (let
               [engine (create-clingo-solver {})
                basic-prog (api/->StringProg ["v(1..3)."])]
               (just-anssets (api/solve engine basic-prog)) =>
               [#{[:v 1] [:v 2] [:v 3]}]))
      )




