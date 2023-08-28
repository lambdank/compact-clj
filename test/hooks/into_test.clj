(ns hooks.into-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.into]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest into->set-test
  (tu/test-example! #'hooks.into/into->set {:col 2 :end-col 6}))

(deftest into->vec-test
  (tu/test-example! #'hooks.into/into->vec {:col 2 :end-col 6}))
