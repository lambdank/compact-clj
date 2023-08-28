(ns hooks.when-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.when]))

(use-fixtures :once tu/mock-reg-finding)

(deftest when->when-not-test
  (tu/test-example! #'hooks.when/when->when-not {:col 2 :end-col 6}))

(deftest when->not-empty-test
  (tu/test-example! #'hooks.when/when->not-empty {:col 2 :end-col 6}))
