(ns hooks.vec-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.vec]))

(use-fixtures :once tu/mock-reg-compression)

(deftest vec->mapv-test
  (tu/test-example! #'hooks.vec/vec->mapv))

(deftest vec->filterv-test
  (tu/test-example! #'hooks.vec/vec->filterv))
