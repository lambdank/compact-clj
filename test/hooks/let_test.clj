(ns hooks.let-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.let]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest let->doto-test
  (tu/test-example! #'hooks.let/let->doto))

(deftest let->when-let-test
  (tu/test-example! #'hooks.let/let->when-let))

(deftest let->if-let-test
  (tu/test-example! #'hooks.let/let->if-let))

(deftest let->when-some-test
  (tu/test-example! #'hooks.let/let->when-some))
