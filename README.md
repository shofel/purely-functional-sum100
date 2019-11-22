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

## Profiling

My first solution appeared incredibly slow: 35 seconds to generate and filter all the options.

#### Tufte
I've accepted the challenge and started to profile. Tufte appeared to be a good tool. One nuance, it wasn't work properly when I put a threading macros inside `profile`.

My initial guess was that the work with collections is suboptimal. Profiling showed that the slowest function is `reduce+-`, which appends signs to the digits and then sum them up.

The further investigation revealed `(apply +)` as the slowest construction. Then it was obvious that either `apply` or `+` is slow. But that is not the case! This confusion was made by laziness of the collections with "resolved" numbers. The very "resolving" numbers was really slow.

#### Try ClojureScript

I realized it only later. But at that moment I was thinking of slow integer math. That was ridiculous and I decided to try run the same code with ClojureScript. And when I pasted it to the REPL `eval` wasn't functioning. So I replaced `(comp eval seq)` with `(fn [[f x]] (f x))`, and it running half a second. A lot faster than half a minute.

#### Narrow Down

At that point I wasn't sure what is the reason: ClojureScript or replaced `(comp eval seq)`. Then I measured the difference between two options:

```
(perf (comp eval seq))
; Elapsed time: 1530.837363 msecs

(perf (fn [[op digit]] (op digit)))
; Elapsed time: 0.860691 msecs
```

More than a thousand time of difference. Either `eval` or `seq` is really slow.

#### The Answer
And what is the root of the slowness?

```
pId            nCalls      50% ≤       Mean      Clock  Tota
defn_-eval      1,000     1.60ms     1.63ms     1.63s     99%
defn_-seq       1,000     1.20μs     1.64μs     1.64ms     0%
```

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
