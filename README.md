# A solution for challenge from Purely Functional TV mailing list.

@see https://purelyfunctional.tv/issues/purelyfunctional-tv-newsletter-352-tip-use-the-right-kind-of-comment-for-the-job/

Write a program that outputs all possibilities to put a + or – or
nothing between the numbers 1-9 (in order) such that the result
is 100. For example:

1 + 2 + 34 - 5 + 67 - 8 + 9 = 100

The function should output a list of strings with these formulas.


## About

The solution is by no means optimal. This is just an exercise on TDD and on Clojure development and exploring Clojure.

I've got lots of fun writing these functions and tests for them.

## Usage

Start tests every time code or tests changed:
``` sh
lein auto test
```

## License

Copyright © 2019 shofel

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
