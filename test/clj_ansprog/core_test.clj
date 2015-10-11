(ns clj-ansprog.core-test
  (:require [midje.sweet :refer :all]
            [clj-ansprog.core :refer :all]))

(facts "about programs "
       (fact
         "Can combine programs"
         (prog-as-lines (combine-programs [(->StringProg ["a :- not b."])
                                        (->StringProg ["b :- not a." "c."])]))
         => ["a :- not b." "b :- not a." "c."]))