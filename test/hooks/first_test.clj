(ns hooks.first-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.first]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest first->ffirst-test
  (tu/test-example! #'hooks.first/first->ffirst {:col 2 :end-col 7}))

(deftest first->second-test
  (tu/test-example! #'hooks.first/first->second {:col 2 :end-col 7}))
