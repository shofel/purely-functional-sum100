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

(ns clj-digitsto100.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

;;; As task mentions, the output should be a list of strings.
;;; Let's make it as the last step.
;;; Then what's the first?

;; Basically, we should make a couple of things:
;;   - the first is decision taking: choose one of three options
;;   - provide inputs for those decisions
;;   - accumulate taken decisions

;; Ok, then. The input is digits from 1 to 9.
;; What is the output?
;;   - a list of all decisions taken?
;;   - a list of digits and signs between them?
;;
;; The first option will require merging back with digits.
;; The second option contain all the info. But `|` changes the number
;; of elements.
;;
;; To not be affected by this, let's use a sequence with all
;; the digits and ops between them.

(defn |
  "An operator which represents decision to glue two digits.
  @example (= (| 1 2) 12)"
  [x y]
  (+ (* 10 x) y))

;;;
;;; Decode |
;;;

(declare decode|')

(defn decode|
  "Apply `decode|'` to a list"
  [x]
  (apply decode|' x))

(defn decode|'
  "Make expression with all the `|` applied"
  ([] [])
  ([x] [x])
  ([x op y & tail]
   (condp = op
     | (decode| (cons (| x y) tail))
     (concat [x op] (decode| (cons y tail))))))

;;;
;;; Decode + -
;;;

(defn sign-sum
  "Given a list like [- 2 + 3 ...],
  make a list of numbers with + or - sign."
  [xs]
  (->> xs
    (partition-all 2)
    (map (fn [[op digit]] (op digit)))
    (apply +)))

(defn decode+-'
  "Resolve an expression which is numbers interleaved with signs"
  ([] 0)
  ([f & rst]
   (+ f (sign-sum rst))))

(defn decode+-
  [xs]
  (apply decode+-' xs))

(comment
  (decode+- [])
  (decode+- [1 + 1])
  )

;;;
;;; Combine `decode|` and `reduce+-`.
;;; It's the final decode.
;;;

(def decode
  "Which number is represented by the sequence?"
  (comp decode+- decode|))


(defn hundred?
  "Check if all taken decisions lead to the sum of 100"
  [expression]
  (-> expression
      decode
      (= 100)))
; (comp #(= 100 %) decode)



;;; At this point we are ready to get the right solutions from a list
;;; of solutions. Let's generate all solutions and take correct ones.

(defn append-tails
  [head tails]
  (let [heads (repeat (count tails) head)]
    (map conj heads tails)))

;;; TODO seq of ops to expression
;;; TODO generate and filter

(defn ops->expression
  [xs]
  (concat [1]
          (mapcat vector xs (range 2 10))))

(defn expr->chars
  [xs]
  (let [opchars {- " - "
                 + " + "
                 | "" }]
    (map
      #(get opchars % %)
      xs)))

(defn render-expression
  [expr]
  (-> expr
      expr->chars
      (str/join)))

(defn render-single-solution
  [s]
  (-> s
      render-expression
      (str " = 100")))

;;; Main

(defn -main'
  []
  (->>
    (combo/selections [+ | -] 8)
    (map ops->expression)
    (filter hundred?)
    (map render-single-solution)
    (println)))

(defn -main
  []
  (time (-main')))


;; Answer
#_((1 + 2 + 3 - 4 + 5 + 6 + 7 | 8 + 9)
   (1 + 2 + 3 | 4 - 5 + 6 | 7 - 8 + 9)
   (1 + 2 | 3 - 4 + 5 + 6 + 7 | 8 - 9)
   (1 + 2 | 3 - 4 + 5 | 6 + 7 + 8 + 9)
   (1 | 2 + 3 + 4 + 5 - 6 - 7 + 8 | 9)
   (1 | 2 + 3 - 4 + 5 + 6 | 7 + 8 + 9)
   (1 | 2 - 3 - 4 + 5 - 6 + 7 + 8 | 9)
   (1 | 2 | 3 + 4 - 5 + 6 | 7 - 8 | 9)
   (1 | 2 | 3 + 4 | 5 - 6 | 7 + 8 - 9)
   (1 | 2 | 3 - 4 - 5 - 6 - 7 + 8 - 9)
   (1 | 2 | 3 - 4 | 5 - 6 | 7 + 8 | 9))

;; Formatted answer
#_(list
    "1 + 2 + 3 - 4 + 5 + 6 + 78 + 9 = 100"
    "1 + 2 + 34 - 5 + 67 - 8 + 9 = 100"
    "1 + 23 - 4 + 5 + 6 + 78 - 9 = 100"
    "1 + 23 - 4 + 56 + 7 + 8 + 9 = 100"
    "12 + 3 + 4 + 5 - 6 - 7 + 89 = 100"
    "12 + 3 - 4 + 5 + 67 + 8 + 9 = 100"
    "12 - 3 - 4 + 5 - 6 + 7 + 89 = 100"
    "123 + 4 - 5 + 67 - 89 = 100"
    "123 + 45 - 67 + 8 - 9 = 100"
    "123 - 4 - 5 - 6 - 7 + 8 - 9 = 100"
    "123 - 45 - 67 + 89 = 100")
