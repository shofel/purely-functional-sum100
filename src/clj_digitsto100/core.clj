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

;; Ok, then. The input is digits from 1 to 9.
;; What is the output?
;;   - a list of all decisions taken?
;;   - a list of digits and signs between them?
;;
;; The first option will require merging back with digits.
;; The second option contain all the info. But | changes the number
;; of an element.
;;
;; To not be affected by this, let's use a sequence with all
;; the digits and ops between them.

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

;;;
;;; Decode |
;;;

(declare decode|')

(defn decode|
  "Apply all | operators in a given seq"
  [x]
  (apply decode|' x))

(defn decode|'
  "Like decode| but takes all args in one list"
  ([] [])
  ([x] [x])
  ([x op y & tail]
   (condp = op
     | (decode| (cons (| x y) tail))
     (concat [x op] (decode| (cons y tail))))))

;;;
;;; Decode+-
;;;

(defn signed-numbers
  "Given a list like [- 2 + 3 ...],
  make a list of numbers with + or - sign."
  [xs]
  (->> xs
       (partition 2)
       (map (comp eval seq))))

(declare reduce+-')

;; TODO find the better way
;;      - a function in core?
;;      - pattern matching syntax?
(defn reduce+-
  "Adapter for reduce+-' for easier pattern matching"
  [x]
  (apply reduce+-' x))

(defn reduce+-'
  "Apply + and - in a given seq."
  ([] 0)
  ([x] x)
  ([x & xs]
   (->> xs
        signed-numbers
        (cons x)
        (apply +))))

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

#_(defn calculate
  "What number the decisions lead to?"
  [decisions]
  {:pre (= 8 (count decisions))}
  (let [op->digit (map vector decisions (rest (digits)))
        op->number (map op-digit)]
    ;; We can treat `+` and `-` as a modifiers for the next number.
    ;; Somewhat making a number from a digit. Then sum all of them.
    ;; But `|` is different. It applies to two digits at once. The
    ;; similarity is it also makes a number, which is to be summed.
    ;;
    ;; Given the above, it's ok to store `+` and `-` in a pair with
    ;; a digit. But we need another way for `|`.


    ;; Firstly, apply all the `|`.
    (-> pairs
      (map numbers))
    (reduce
      (fn [acc [f digit]] (f acc digit))
      1
      pairs)))

#_(def decisions [+ + | - + | - +])

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
