(ns hooks.minus-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.minus]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def -->inc-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:int]
                                  [:double {:gen/NaN? false}]])]
                (let [input (str `(- ~x -1))]
                  (tu/equivalent? hooks.minus/-->inc input))))

(deftest -->inc-test
  (tu/test-example! #'hooks.minus/-->inc)
  (is (:pass? (tc/quick-check 100 -->inc-equivalent-property))))

(def -->dec-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:int]
                                  [:double {:gen/NaN? false}]])]
                (let [input (str `(- ~x 1))]
                  (tu/equivalent? hooks.minus/-->dec input))))

(deftest -->dec
  (tu/test-example! #'hooks.minus/-->dec)
  (is (:pass? (tc/quick-check 100 -->dec-equivalent-property))))
