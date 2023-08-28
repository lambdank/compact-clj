(ns hooks.minus-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.minus]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest -->inc-test
  (tu/test-example! #'hooks.minus/-->inc {:col 2 :end-col 3}))

(deftest -->dec
  (tu/test-example! #'hooks.minus/-->dec {:col 2 :end-col 3}))
