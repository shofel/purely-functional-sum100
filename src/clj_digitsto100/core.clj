;;;
;;; @see https://purelyfunctional.tv/issues/purelyfunctional-tv-newsletter-352-tip-use-the-right-kind-of-comment-for-the-job/
;;;
;;; Write a program that outputs all possibilities to put a + or – or
;;; nothing between the numbers 1-9 (in order) such that the result
;;; is 100. For example:
;;;
;;; 1 + 2 + 34 - 5 + 67 - 8 + 9 = 100
;;;
;;; The function should output a list of strings with these formulas.
;;;

(ns clj-digitsto100.core)

;;; As task mentions, the output should be a list of strings.
;;; Let's make it as the last step.
;;; Then what's the first?

;; Ok, first of all, the input is always the list of digits 1..9.
(defn digits
  "List of digits from 1 to 9"
  []
  (range 1 10))

;; Basically, we should make a couple of things:
;;   - the first is decision taking: choose one of three options
;;   - provide inputs for those decisions
;;   - accumulate taken decisions

;; Unavoidably, we will iterate over places between the digits.
;; And accumulate the result.
;; Look Ma this is a `reduce`!

;; Ok, then. The input is digits from 1 to 9.
;; What is the output?
;;   - a list of all decisions taken?
;;   - a list of digits and signs between them?
;;
;; The first option will require merging back with digits.
;; The second option contain all the info.
;;
;; Then let's go with the second

;; Decision making function.
;; TODO really make a decision
(defn step
  ""
  [acc [first & rest]]
  {:pre [(number? first)]}
  [(conj acc first) rest])

(defn |
  "An operator which represents decision to glue two digits.
  @example (= (. 1 2) 12)"
  [x y]
  (+ (* 10 x) y))

(defn calculate
  "What number the decisions lead to?"
  [decisions]
  {:pre (= 8 (count decisions))}
  (let [pairs (map vector decisions (rest (digits)))]
    ;; Firstly, apply all the `|`.
    (-> pairs
      (map numbers))
    (reduce
      (fn [acc [f digit]] (f acc digit))
      1
      pairs)))

#_(calculate [+ + | - + | - +])

;; It'd be easier to have two different lists:
;; - one with digits from 1 to 9
;; - one with taken decisions
;; Then zip them, and reduce to a number.
(defn hundred?
  "Check if all taken decisions lead to the sum of 100"
  [decisions]

)

(map vector (digits) (rest (digits)))

;; Let's walk the tree of decisions
#_(defn walk-step
  "Do one step of the walk"
  [state rest]
  (if (empty? rest) state ; done
    ; plus
    ; minus
    ; glue
    )
  )
