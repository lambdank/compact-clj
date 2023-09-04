(ns hooks.not-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.not]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def not->not=-equivalent-property
  (prop/for-all [xs (tu/generator [:sequential {:min 1 :max 10} ::tu/not-symbol])]
                (let [;; Make sure some lists have equal elements
                      ys (cond-> xs (= 1 (count xs)) (conj (first xs)))
                      input (str `(~'not (~'= ~@ys)))]
                  (tu/equivalent? hooks.not/not->not= input))))

(deftest not->not=-test
  (tu/test-example! #'hooks.not/not->not=)
  (is (:pass? (tc/quick-check 100 not->not=-equivalent-property))))

(def not->not-any?-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'not (~'some ~f ~coll)))]
                  (tu/equivalent? hooks.not/not->not-any? input))))

(deftest not->not-any?-test
  (tu/test-example! #'hooks.not/not->not-any?)
  (is (:pass? (tc/quick-check 100 not->not-any?-equivalent-property))))

(def not->not-every?-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'not (~'every? ~f ~coll)))]
                  (tu/equivalent? hooks.not/not->not-every? input))))

(deftest not->not-every?-test
  (tu/test-example! #'hooks.not/not->not-every?)
  (is (:pass? (tc/quick-check 100 not->not-every?-equivalent-property))))

(def not->seq-equivalent-property
  (prop/for-all [coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'not (~'empty? ~coll)))]
                  (tu/equivalent? hooks.not/not->seq input
                                  {:out-post boolean}))))

(deftest not->seq-test
  (tu/test-example! #'hooks.not/not->seq)
  (is (:pass? (tc/quick-check 100 not->seq-equivalent-property))))

(def not->odd?-equivalent-property
  (prop/for-all [n (tu/generator :int)]
                (let [input (str `(~'not (~'even? ~n)))]
                  (tu/equivalent? hooks.not/not->odd? input))))

(deftest not->odd?-test
  (tu/test-example! #'hooks.not/not->odd?)
  (is (:pass? (tc/quick-check 100 not->odd?-equivalent-property))))

(def not->even?-equivalent-property
  (prop/for-all [n (tu/generator :int)]
                (let [input (str `(~'not (~'odd? ~n)))]
                  (tu/equivalent? hooks.not/not->even? input))))

(deftest not->even?-test
  (tu/test-example! #'hooks.not/not->even?)
  (is (:pass? (tc/quick-check 100 not->even?-equivalent-property))))

(def not->some?-equivalent-property
  (prop/for-all [x (tu/generator ::tu/not-symbol)]
                (let [input (str `(~'not (~'nil? ~x)))]
                  (tu/equivalent? hooks.not/not->some? input))))

(deftest not->some?-test
  (tu/test-example! #'hooks.not/not->some?)
  (is (:pass? (tc/quick-check 100 not->some?-equivalent-property))))

(def not->empty?-equivalent-property
  (prop/for-all [coll (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'not (~'seq ~coll)))]
                  (tu/equivalent? hooks.not/not->empty? input))))

(deftest not->empty?-test
  (tu/test-example! #'hooks.not/not->empty?)
  (is (:pass? (tc/quick-check 100 not->empty?-equivalent-property))))
