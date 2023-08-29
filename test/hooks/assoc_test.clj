(ns hooks.assoc-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.assoc]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest assoc-remove-nested-test
  (is (= {:row 1
          :end-row 1
          :col 9
          :end-col 14
          :message (u/->msg "(assoc m :a x)" "m :a x")
          :type :compact-clj/assoc-remove-nested}
         (hooks.assoc/assoc-remove-nested
          (api/parse-string (-> #'hooks.assoc/assoc-remove-nested meta :example :in str))))))

(deftest assoc->assoc-in-test
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:col 2 :end-col 7})
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:col 2 :end-col 7
                                                   :in '(assoc m :a (assoc (m :a) :b x))})
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:col 2 :end-col 7
                                                   :in '(assoc m :a (assoc (get m :a) :b x))}))
(deftest assoc->update-test
  (tu/test-example! #'hooks.assoc/assoc->update {:col 2 :end-col 7})
  (tu/test-example! #'hooks.assoc/assoc->update {:col 2 :end-col 7
                                                 :in '(assoc m k (f (m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:col 2 :end-col 7
                                                 :in '(assoc m k (f (get m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:col 2 :end-col 7
                                                 :in '(assoc m k (f (m k) x y z))
                                                 :out '(update m k (f x y z))}))
