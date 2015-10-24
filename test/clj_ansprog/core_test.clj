(ns clj-ansprog.core-test
  (:require [midje.sweet :refer :all]
            [clj-ansprog.core :refer :all]))

(facts "about programs "
       (fact
         "Can combine programs"
         (prog-as-lines (combine-programs [(->StringProg ["a :- not b."])
                                        (->StringProg ["b :- not a." "c."])]))
         => ["a :- not b." "b :- not a." "c."]))


(facts "about filtering answer sets"
       (fact
         "can filter terms in answer sets for single terms "
         (filter (term-filter :a) #{[:a] [:a 2] [:b]}) => [[:a]])

       (fact
         "can filter terms in answer sets for term with arity terms "
         (filter (term-filter :a 1) #{[:a] [:a 2] [:b]}) => [[:a 2]])
       (fact
         "can filter terms in answer sets for term with arity terms "
         (filter (term-filter :a 2) #{[:a] [:a 2] [:b]}) => [])
       )