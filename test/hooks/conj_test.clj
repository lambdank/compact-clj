(ns hooks.conj-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.conj]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest conj-remove-nested-test
  (is (= {:row 1
          :end-row 1
          :col 8
          :end-col 12
          :message (u/->msg "(conj coll x)" "coll x")
          :type :compact-clj/conj-remove-nested}
         (hooks.conj/conj-remove-nested
          (api/parse-string (-> #'hooks.conj/conj-remove-nested meta :example :in str))))))
