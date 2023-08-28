(ns hooks.when-some-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.test-utils :as tu]
   [hooks.when-some]))

(use-fixtures :once tu/mock-reg-finding)

(deftest when-some->some->-test
  (tu/test-example! #'hooks.when-some/when-some->some-> {:col 2 :end-col 11}))
