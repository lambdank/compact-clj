(ns hooks.let-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.let]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest let->doto-test
  (tu/test-example! #'hooks.let/let->doto {:col 2 :end-col 5}))

(deftest let->when-let-test
  (tu/test-example! #'hooks.let/let->when-let {:col 2 :end-col 5}))

(deftest let->if-let-test
  (tu/test-example! #'hooks.let/let->if-let {:col 2 :end-col 5}))

(deftest let->when-some-test
  (tu/test-example! #'hooks.let/let->when-some {:col 2 :end-col 5}))
