(ns hooks.comp-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.comp]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest or-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 8
                :end-col 12
                :message (u/->msg "(comp f g)" "f g")
                :type :compact-clj/comp-remove-nested})
         (hooks.comp/comp-remove-nested (api/parse-string (-> #'hooks.comp/comp-remove-nested meta :example :in str))))))
