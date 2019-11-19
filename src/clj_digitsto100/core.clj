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
;; For the beginning: the decision is always noop.
(defn step
  ""
  [acc [first & rest]]
  [(conj acc first) rest])
