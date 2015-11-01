(ns clj-aspviz.rotations-test
  (:require [clj-ansprog.rotations :refer :all]
            [midje.sweet :refer :all]))

(def tp [:part [:p 1 1 1]])
(def tpiece [ [:part [:p 0 0 0]]
            [:part [:p 1 0 0]]
            [:part [:p 0 1 0]]
            ])


(facts "about rotations "
       (fact "can rotate part "
             (rotate-part [0,0,0] tp) => tp
             (rotate-part [1,0,0] tp) => [:part [:p 1 -1 1]])
       (fact "generates piece rotations for a compound piece "
             (gen-rotations [[:part [:p  0 0 0]]
                             [:part [:p  1 0 0]]
                             [:part [:p  2 0 0]]
                             ]) =>
             #{#{[:part [:p 2 0 0]] [:part [:p 0 0 0]] [:part [:p 1 0 0]]}
               #{[:part [:p 0 0 2]] [:part [:p 0 0 0]] [:part [:p 0 0 1]]}
               #{[:part [:p 0 1 0]] [:part [:p 0 0 0]] [:part [:p 0 2 0]]}})

       (fact "generates piece rotations for a simple piece "
             (gen-rotations [[:part [:p  0 0 5]]]) =>
             #{#{[:part [:p 0 0 0]]}})


       )



