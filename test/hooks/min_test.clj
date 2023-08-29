(ns hooks.min-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.min]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest min-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 7
                :end-col 10
                :message (u/->msg "(min x y)" "x y")
                :type :compact-clj/min-remove-nested})
         (hooks.min/min-remove-nested (api/parse-string (-> #'hooks.min/min-remove-nested meta :example :in str))))))
