(ns hooks.vec-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.vec]))

(use-fixtures :once tu/mock-reg-finding)

(deftest vec->mapv-test
  (tu/test-example! #'hooks.vec/vec->mapv {:col 2 :end-col 5}))

(deftest vec->filterv-test
  (tu/test-example! #'hooks.vec/vec->filterv {:col 2 :end-col 5}))
