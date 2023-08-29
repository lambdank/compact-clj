(ns hooks.eq-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.eq]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest =-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 5
                :end-col 6
                :message (u/->msg "(= x y)" "x y")
                :type :compact-clj/=-remove-nested})
         (hooks.eq/=-remove-nested (api/parse-string (-> #'hooks.eq/=-remove-nested meta :example :in str))))))

(deftest =->true?-test
  (tu/test-example! #'hooks.eq/=->true? {:col 2 :end-col 3}))

(deftest =->nil?-test
  (tu/test-example! #'hooks.eq/=->nil? {:col 2 :end-col 3}))

(deftest =->emtpy?-test
  (tu/test-example! #'hooks.eq/=->empty? {:col 2 :end-col 3})
  (tu/test-example! #'hooks.eq/=->empty? {:col 2 :end-col 3
                                          :in '(= (count coll) 0)}))

(deftest =-remove-duplicate-test
  (tu/test-example! #'hooks.eq/=-remove-duplicate {:col 2 :end-col 3}))
