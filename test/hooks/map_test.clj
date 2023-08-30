(ns hooks.map-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.map]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest map-remove-nested-test
  (tu/test-example! #'hooks.map/map-remove-nested))
