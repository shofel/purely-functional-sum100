(ns clj-digitsto100.core-test
  (:require [clojure.test :refer :all]
            [clj-digitsto100.core :refer :all]))


(deftest digits-test
  (testing "Digits are the range from 1 to 9"
    (is (= (digits) [1 2 3 4 5 6 7 8 9]))))

(deftest step-test
  (testing "Step moves a digit from rest to acc"
    (is (= (step [] [1 2 3]) [[1] [2 3]]))
    (is (= (step [] [1]) [[1] nil]))
    (is (thrown? AssertionError (step [1] [])))))
