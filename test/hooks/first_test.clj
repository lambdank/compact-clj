(ns hooks.first-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.first]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def first->ffirst-equivalent-property
  (prop/for-all [coll (tu/generator [:sequential {:min 0 :max 10}
                                     ::tu/seq-not-symbol])]
                (let [input (str `(~'first (~'first ~coll)))]
                  (tu/equivalent? hooks.first/first->ffirst input))))

(deftest first->ffirst-test
  (tu/test-example! #'hooks.first/first->ffirst)
  (is (:pass? (tc/quick-check 100 first->ffirst-equivalent-property))))

(def first->second-equivalent-property
  (prop/for-all [coll (tu/generator [:sequential {:min 0 :max 10}
                                     ::tu/seq-not-symbol])]
                (let [input (str `(~'first (~'next ~coll)))]
                  (tu/equivalent? hooks.first/first->second input))))

(deftest first->second-test
  (tu/test-example! #'hooks.first/first->second)
  (is (:pass? (tc/quick-check 100 first->second-equivalent-property))))
