(ns hooks.minus-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.minus]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest -->inc-test
  (tu/test-example! #'hooks.minus/-->inc))

(deftest -->dec
  (tu/test-example! #'hooks.minus/-->dec))
