(ns hooks.map-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.map]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest map-remove-nested-test
  (tu/test-example! #'hooks.map/map-remove-nested {:col 2 :end-col 5}))
