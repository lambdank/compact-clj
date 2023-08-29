(ns hooks.max-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.max]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest max-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 7
                :end-col 10
                :message (u/->msg "(max x y)" "x y")
                :type :compact-clj/max-remove-nested})
         (hooks.max/max-remove-nested (api/parse-string (-> #'hooks.max/max-remove-nested meta :example :in str))))))

