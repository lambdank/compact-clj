(ns hooks.or-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.or]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest or->some-test
  (tu/test-example! #'hooks.or/or->some {:col 2 :end-col 4}))

(deftest or->get-test
  (tu/test-example! #'hooks.or/or->get {:col 2 :end-col 4})
  (tu/test-example! #'hooks.or/or->get {:col 2 :end-col 4
                                        :in '(or (get m k) x)}))
