(ns hooks.when-some-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.when-some]))

(use-fixtures :once tu/mock-reg-compression)

(deftest when-some->some->-test
  (tu/test-example! #'hooks.when-some/when-some->some->))
