(ns hooks.into-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.into]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def into->set-equivalent-property
  (prop/for-all [coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'into #{} ~coll))]
                  (tu/equivalent? hooks.into/into->set input))))

(deftest into->set-test
  (tu/test-example! #'hooks.into/into->set)
  (is (:pass? (tc/quick-check 100 into->set-equivalent-property))))

(def into->vec-equivalent-property
  (prop/for-all [coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'into [] ~coll))]
                  (tu/equivalent? hooks.into/into->vec input))))

(deftest into->vec-test
  (tu/test-example! #'hooks.into/into->vec)
  (is (:pass? (tc/quick-check 100 into->vec-equivalent-property))))
