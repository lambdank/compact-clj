(ns hooks.and-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.and]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest and-remove-nested
  (is (= (list {:row 1
                :end-row 1
                :col 7
                :end-col 10
                :message (u/->msg "(and x y)" "x y")
                :type :lol})
         (hooks.and/and-remove-nested (api/parse-string (-> #'hooks.and/and-remove-nested meta :example :in str))))))

(deftest and->every?
  (tu/test-example! #'hooks.and/and->every? {:col 2 :end-col 5}))
