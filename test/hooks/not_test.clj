(ns hooks.not-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.not]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest not->not=-test
  (tu/test-example! #'hooks.not/not->not=))

(deftest not->not-any?-test
  (tu/test-example! #'hooks.not/not->not-any?))

(deftest not->boolean-test
  (tu/test-example! #'hooks.not/not->boolean))

(deftest not->not-every?-test
  (tu/test-example! #'hooks.not/not->not-every?))

(deftest not->seq-test
  (tu/test-example! #'hooks.not/not->seq))

(deftest not->odd?-test
  (tu/test-example! #'hooks.not/not->odd?))

(deftest not->even?-test
  (tu/test-example! #'hooks.not/not->even?))

(deftest not->true?-test
  (tu/test-example! #'hooks.not/not->true?))

(deftest not->false?-test
  (tu/test-example! #'hooks.not/not->false?))

(deftest not->some?-test
  (tu/test-example! #'hooks.not/not->some?))

(deftest not->empty?-test
  (tu/test-example! #'hooks.not/not->empty?))
