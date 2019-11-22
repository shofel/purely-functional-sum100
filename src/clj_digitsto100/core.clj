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
  (:require [clojure.string :as str]
            [taoensso.tufte
             :as tufte
             :refer (defnp p profile)]))

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

(defnp decode|'
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

(defn sum'
  "Given a list like [- 2 + 3 ...],
  make a list of numbers with + or - sign."
  [xs]
  (->> xs
    (partition-all 2)
    (map (fn [[op digit]] (op digit)))
    (apply +)))

(comment
  ;; perf of sum(map (comp eval seq))ming [- 2 + 3 ...]

  (defn sample
    "Pairs of a sign and a number."
    [n]
    (repeat n [+ n]))

  (defn perf
    [f]
    (time (dotimes [_ 100]
            (->> (sample 10)
                 (map f)
                 (apply +)))) )

  ; Elapsed time: 1530.837363 msecs
  (perf (comp eval seq))

  ; Elapsed time: 0.860691 msecs
  (perf (fn [[op digit]] (op digit)))

  (defnp -eval [& args] (apply eval args))
  (defnp -seq [& args] (apply seq args))

  ;; What is slow:`comp` `eval` or `seq` ??
  ;;
  ;; pId            nCalls      50% ≤       Mean      Clock  Total
  ;; defn_-eval      1,000     1.60ms     1.63ms     1.63s     99%
  ;; defn_-seq       1,000     1.20μs     1.64μs     1.64ms     0%
  (profile
    {}
    (dotimes [_ 100]
      (let [xs (sample 10)
            numbers (map
                      (comp -eval -seq)
                      xs)
            sum (apply + numbers)]
        sum)))
  )

(declare reduce+-')

;; TODO find the better way
;;      - a function in core?
;;      - pattern matching syntax?
(defn reduce+-
  "Adapter for reduce+-' for easier pattern matching"
  [x]
  (apply reduce+-' x))

(defnp plus
  [& xs]
  (apply + xs))

(defnp reduce+-'
  "Apply + and - in a given seq."
  ([] 0)
  ([x] x)
  ([x & xs]
   (p :plus-body (+ x (sum' xs)))))

;;;
;;; Combine `decode|` and `reduce+-`.
;;; It's the final decode.
;;;

(defn decode
  "Which number is represented by the sequence?"
  [xs]
  (-> xs
      decode|
      reduce+-))

;; It'd be easier to have two different lists:
;; - one with digits from 1 to 9
;; - one with taken decisions
;; Then zip them, and reduce to a number.
(defn hundred?
  "Check if all taken decisions lead to the sum of 100"
  [expression]
  (-> expression
      decode
      (= 100)))



;;; At this point we are ready to get the right solutions from a list
;;; of solutions. Let's generate all solutions and take correct ones.

(defn append-tails
  [head tails]
  (let [heads (repeat (count tails) head)]
    (map conj heads tails)))

(defn combinations
  "Generate all combinations for a given alphabet of a given length"
  ([length alphabet]
   (condp = length
     (combinations [[]] length alphabet)))
  ([heads length alphabet]
   (if (zero? length) heads
     (combinations
       (mapcat
         #(append-tails % alphabet)
         heads)
       (dec length)
       alphabet))))


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

;;; TODO make expression ops always looking well

(defn -main
  []
  (->>
    (combinations 8 [+ | -])
    (map ops->expression)
    (filter hundred?)
    (map render-single-solution)
    (map println)))

#_(-main)

;;; Profile
(tufte/add-basic-println-handler!
{:format-pstats-opts {:columns [:n-calls :p50 :mean :clock :total]
                      :format-id-fn name}})

;;; perf

(comment
  (defn -combos
    []
    (combinations 8 [+ | -]))

  ;; Combinations is fast, lets measure the rest.

  ; 0s: -combos
  (time (->> (-combos)
             doall
             (take 0)))

  (def combos (-combos))

  ;; 0s: ops->expression
  (time (->> combos
             (map ops->expression)
             doall
             (take 0)))

  ;; ?s: sum numbers
  (time (dotimes [n 10e3]
          (sum' (interleave
                  (repeat n :+)
                  (repeat n n)))))
  )

#_
(do
  (defn expressions
    []
    (map ops->expression
         (combinations 8 [+ | -])))

  ;; 35s: filter hundred?
  (profile
    {}
    (take 0 (doall
              (filter hundred? (expressions))))))

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
