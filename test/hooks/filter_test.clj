(ns hooks.filter-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.filter]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def filter->remove-equivalent-property
  (prop/for-all [neg (tu/generator [:or
                                    [:= 'not]
                                    [:= 'complement]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [pred 'identity
                      not-pred (if (= neg 'not)
                                 `(~'fn [~'x] (~'not (~pred ~'x)))
                                 `(~'complement ~pred))
                      input (str `(~'filter ~not-pred ~coll))]
                  (tu/equivalent? hooks.filter/filter->remove input))))

(deftest filter->remove-test
  (tu/test-example! #'hooks.filter/filter->remove)
  (tu/test-example! #'hooks.filter/filter->remove {:in "(filter #(not (f %)) coll)"
                                                   :out "(remove #(f %) coll)"})
  (tu/test-example! #'hooks.filter/filter->remove {:in '(filter (complement f) coll)
                                                   :out '(remove f coll)})
  (is (:pass? (tc/quick-check 100 filter->remove-equivalent-property))))

(def filter->keep-equivalent-property
  (prop/for-all [pred (tu/generator [:or
                                     [:= 'not]
                                     [:= 'identity]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'filter ~'some? (~'map ~pred ~coll)))]
                  (tu/equivalent? hooks.filter/filter->keep input))))

(deftest filter->keep-test
  (tu/test-example! #'hooks.filter/filter->keep)
  (is (:pass? (tc/quick-check 100 filter->keep-equivalent-property))))
