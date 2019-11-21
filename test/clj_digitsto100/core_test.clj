(ns clj-digitsto100.core-test
  (:require [clojure.test :refer :all]
            [clj-digitsto100.core :refer :all]))


(deftest digits-test
  (testing "Digits are the range from 1 to 9"
    (is (= (digits) [1 2 3 4 5 6 7 8 9]))))

(deftest step-test
  (testing "Step moves a digit from rest to acc"
    (is (= (step [] [1 2 3]) [[1] [2 3]])))

  (testing "The last step"
    (is (= (step [] [1]) [[1] nil])))

  (testing "Throw when no more steps"
    (is (thrown? AssertionError (step [1] [])))))


(deftest decode|-test
  (testing "No |"
    (is (= (decode| []) []))
    (is (= (decode| [3]) [3])))
  (testing "Applies | correctly"
    (is (= (decode| [1 | 1]) [11]))
    (is (= (decode| [1 | 2 | 3]) [123])))
  (testing "Preserves + and -"
    (is (= (decode| [1 + 1]) [1 + 1]))
    (is (= (decode| [1 - 1]) [1 - 1])))
  (testing "Combinations of | + -"
    (is (= (decode| [1 | 2 - 3]) [12 - 3]))
    (is (= (decode| [1 | 2 + 3]) [12 + 3])))
  (testing "When no | at the first place"
    (is (= (decode| [1 + 2 | 3]) [1 + 23]))))


(deftest signed-numbers-test
  (testing "all?"
    (is (= (signed-numbers [+ 1]) [1]))
    (is (= (signed-numbers [- 1]) [-1]))
    (is (= (signed-numbers [- 1 - 2]) [-1 -2]))))


(deftest reduce+--test
  (testing "Just a value"
    (is (= (reduce+- []) 0))
    (is (= (reduce+- [1]) 1))
    (is (= (reduce+- [1 - 2]) -1))))


(deftest decode-test
  #_(testing "Let's start with |"
    (is (= (decode [1 | 2]) 12))
    (is (= (decode [1 | 2 | 3]) 123)))
  #_(testing "The right precedence | vs +"
        (= (decode [1 + 2 | 3]) 24)))


(deftest |-test
  (testing "Only trivial cases. No leading zero; only single digits"
    (is (= (| 1 2) 12))
    (is (= (| 3 5) 35))
    (is (= (| 6 0) 60))))

#_(deftest hundred?-test
  (testing "A solution from the text of the task"
    (is (= (hundred? [+ + | - + | - +]) true)))
  (testing "Obviously wrong: sum all the digits"
    (is (= (hundred? [+ + + + + + + +]) false))))

#_(do
;;;
;;; Playground for `conj` `cons` `concat` `into` `mapcat`
;;;

;; `conj`: take the first arg; append the rest
;;   to the most efficient location possible
(conj {:a 1} [:c 3])
(conj {:a 1} [:d 3] {:d "D" :e 'E})
(conj [3] '(4)) ; -> [3 (4)]
(conj [3] 4) ; -> [3 4]
;; Not necessarily to the end:
(conj `(1) 2 3)

;; `cons`: construct a sequence. Returns a seq.
;; : head, tail -> seq
(cons 1 `(2))
;(cons 1 `(2) `(3)) ; wrong arity

;; `concat`
(concat [1 2] [3] [4])
(concat [1 [2]] [3] [4]) ; reduces only one level
(reduce
  (fn [acc x] (conj acc x)) ; or just `conj` :)
  {}
  (concat {:a 1 :b 2} {:c 3}))

;; into: `conj` every item into the first arg
; The same as `reduce` above =)
(into {} (concat {:a 1 :b 2} {:c 3}))


(into {} (map (fn [[k v]] [k (inc v)]) {1 2 3 4}))
)
