(ns hooks.when-let-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.when-let]))

(use-fixtures :once tu/mock-reg-finding)

(deftest when-let->when-first
  (tu/test-example! #'hooks.when-let/when-let->when-first {:col 2 :end-col 10}))
