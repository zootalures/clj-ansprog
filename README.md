# clj-ansprog

This is a clojure wrapper for interacting with answer set solvers. At present it supports the
[Potassco](http://potassco.sourceforge.net/)  *clingo*
solver.

It's here to help  clojure developers use answer set programming (ASP) within their own programs

The tools require that you have *clingo*  (tested with 4.5.2)  installed and on your path - this is available via the
*gringo* and *clasp* packages in many package managers (brew/ubuntu etc.).

# Usage

The library is a fairly course wrapper around the underlying system programs  -
     it generates answer sets as parsed clojure data structures .


## Solving and parsing answer sets

The  code below shows creating  simple program:

    a :- not b.
    b :- not a.


        (ns clj-ansprog.example
            (:require
              [clj-ansprog.core :as asp]
              [clj-ansprog.clingo-solver :as clingo]))

        (def solver (clingo/create-clingo-solver {:solver-path "clingo"}))

        (def prog (asp/string->program "a :- not b. \n b :- not a."))

        (map asp/all-terms  (asp/solve solver prog))

        =>
        ((:a) (:b))


## License

Copyright © 2015 Owen Cliffe

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
