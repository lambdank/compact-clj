(ns hooks.first-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.first]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest first->ffirst-test
  (tu/test-example! #'hooks.first/first->ffirst))

(deftest first->second-test
  (tu/test-example! #'hooks.first/first->second))
