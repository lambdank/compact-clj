(ns hooks.when-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.test-utils :as tu]
   [hooks.when]))

(use-fixtures :once tu/mock-reg-compression)

(def when->when-not-equivalent-property
  (prop/for-all [bool (tu/generator [:or
                                     [:cat [:or
                                            [:= 'identity]
                                            [:= 'not]]
                                      :boolean]
                                     :boolean])
                 body (tu/generator [:sequential {:min 1 :max 10} ::tu/not-symbol])]
                (let [input (str `(~'when (~'not ~bool) ~@body))]
                  (tu/equivalent? hooks.when/when->when-not input))))

(deftest when->when-not-test
  (tu/test-example! #'hooks.when/when->when-not)
  (is (:pass? (tc/quick-check 100 when->when-not-equivalent-property))))

(def when->not-empty-equivalent-property
  (prop/for-all [coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'when (~'seq ~coll) ~coll))]
                  (tu/equivalent? hooks.when/when->not-empty input))))

(deftest when->not-empty-test
  (tu/test-example! #'hooks.when/when->not-empty)
  (is (:pass? (tc/quick-check 100 when->not-empty-equivalent-property))))
