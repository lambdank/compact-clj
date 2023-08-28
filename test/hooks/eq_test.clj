(ns hooks.eq-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.eq]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest =->true?-test
  (tu/test-example! #'hooks.eq/=->true? {:col 2 :end-col 3}))

(deftest =->nil?-test
  (tu/test-example! #'hooks.eq/=->nil? {:col 2 :end-col 3}))

(deftest =->emtpy?-test
  (tu/test-example! #'hooks.eq/=->empty? {:col 2 :end-col 3})
  (tu/test-example! #'hooks.eq/=->empty? {:col 2 :end-col 3
                                          :in '(= (count coll) 0)}))
