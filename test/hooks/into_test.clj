(ns hooks.into-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.into]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest into->set-test
  (tu/test-example! #'hooks.into/into->set))

(deftest into->vec-test
  (tu/test-example! #'hooks.into/into->vec))
