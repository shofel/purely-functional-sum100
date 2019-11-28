(ns clj-digitsto100.core-test
  (:require [clojure.test :refer :all]
            [clj-digitsto100.core :refer :all]))


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


(deftest decode+--test
  (testing "all?"
    (is (= (decode+- []) 0))
    (is (= (decode+- [1 + 1]) 2))
    (is (= (decode+- [1 - 1]) 0))
    (is (= (decode+- [1 - 2 + 3]) 2))))


#_(deftest decode-test
  (testing "Let's start with |"
    (is (= (decode [1 | 2]) 12))
    (is (= (decode [1 | 2 | 3]) 123)))
  (testing "+ and -"
    (is (= (decode [1 + 2 - 3]) 0)))
  (testing "The right precedence | vs +"
    (is (= (decode [1 + 2 | 3]) 24))
    (is (= (decode [1 + 2 | 3 - 4]) 20))))


(deftest |-test
  (testing "Only trivial cases. No leading zero; only single digits"
    (is (= (| 1 2) 12))
    (is (= (| 3 5) 35))
    (is (= (| 6 0) 60))))

#_(deftest hundred?-test
  (testing "A solution from the text of the task"
    (is (= (hundred? [1 + 2 + 3 | 4 - 5 + 6 | 7 - 8 + 9]) true)))
  (testing "Obviously wrong: sum all the digits"
    (is (= (hundred? [1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9]) false))))



(deftest append-tails-test
  (testing "??"
    (is (= (append-tails [1 2 3] [7 8])
           [[1 2 3 7] [1 2 3 8]]))))

(deftest combinations-test
  (testing "The empty combination of zero-length"
    (is (= (combinations 0 []) [[]])))
  (testing "Length 1 is each letter"
    (is (= (combinations 1 [6 7]) [[6] [7]])))
  (testing "Real combos"
    (is (= (set (combinations 2 [1 2]))
           #{[1 1] [1 2] [2 1] [2 2]})))
  (testing "Length of the list"
    (is (= (count (combinations 3 [1 2 3])) 27))
    (is (= (count (combinations 4 [1 2])) 16))))


(deftest opt->expression-test
  (testing "Augment ops with digits"
    (is (= (ops->expression [+ - |])
           [1 + 2 - 3 | 4]))))

;;; to string

(deftest render-one-expression-test
  (testing "render expression"
    (is (= (->
             [- | +]
             ops->expression
             render-expression)
           "1 - 23 + 4"))))

(deftest render-single-solution-test
  (testing "render one solution"
    (is (= (->
             [- | +]
             ops->expression
             render-single-solution)
           "1 - 23 + 4 = 100"))))

(deftest expr->chars-test
  (testing "expr to chars"
    (is (= (expr->chars [1 + 2]) [1 " + " 2]))))
