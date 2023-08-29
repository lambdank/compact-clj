(ns hooks.or-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.or]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest or-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 6
                :end-col 8
                :message (u/->msg "(or x y)" "x y")
                :type :compact-clj/or-remove-nested})
         (hooks.or/or-remove-nested (api/parse-string (-> #'hooks.or/or-remove-nested meta :example :in str))))))

(deftest or->some-test
  (tu/test-example! #'hooks.or/or->some {:col 2 :end-col 4}))

(deftest or->get-test
  (tu/test-example! #'hooks.or/or->get {:col 2 :end-col 4})
  (tu/test-example! #'hooks.or/or->get {:col 2 :end-col 4
                                        :in '(or (get m k) x)}))
