# clj-ansprog

This is a clojure wrapper for interacting with answer set solvers. At present it supports the
[Potassco](http://potassco.sourceforge.net/)  *clingo*
solver.

It's here to help  clojure developers use answer set programming (ASP) within their own programs

The tools require that you have *clingo*  (tested with 4.5.2)  installed and on your path - this is available via the
*gringo* and *clasp* packages in many package managers (brew/ubuntu etc.).

# Usage

## Solving and parsing answer sets


   (def solver (create-clingo-solver))

   (def program ())


## License

Copyright © 2015 Owen Cliffe

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
