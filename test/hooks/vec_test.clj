(ns hooks.vec-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.test-utils :as tu]
   [hooks.vec]))

(use-fixtures :once tu/mock-reg-compression)

(def vec->mapv-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'vec (~'map ~f ~coll)))]
                  (tu/equivalent? hooks.vec/vec->mapv input))))

(deftest vec->mapv-test
  (tu/test-example! #'hooks.vec/vec->mapv)
  (is (:pass? (tc/quick-check 100 vec->mapv-equivalent-property))))

(def vec->filterv-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'vec (~'filter ~f ~coll)))]
                  (tu/equivalent? hooks.vec/vec->filterv input))))

(deftest vec->filterv-test
  (tu/test-example! #'hooks.vec/vec->filterv)
  (is (:pass? (tc/quick-check 100 vec->filterv-equivalent-property))))
