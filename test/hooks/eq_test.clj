(ns hooks.eq-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.eq]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest =-remove-nested-test
  (is (= (list {:root-node "(= x y)"
                :compression "x y"
                :type :compact-clj/=-remove-nested})
         (->> (hooks.eq/=-remove-nested (api/parse-string (-> #'hooks.eq/=-remove-nested meta :example :in str)))
              (map #(dissoc % :highlight-node))))))

(def =->true?-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:ref ::tu/not-symbol]
                                  [:ref ::tu/seq-not-symbol]])]
                (let [input (str (cons '= (shuffle `(~x ~'true))))]
                  (tu/equivalent? hooks.eq/=->true? input))))

(deftest =->true?-test
  (tu/test-example! #'hooks.eq/=->true?)
  (is (:pass? (tc/quick-check 100 =->true?-equivalent-property))))

(def =->nil?-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:ref ::tu/not-symbol]
                                  [:ref ::tu/seq-not-symbol]])]
                (let [input (str (cons '= (shuffle `(~x ~'nil))))]
                  (tu/equivalent? hooks.eq/=->nil? input))))

(deftest =->nil?-test
  (tu/test-example! #'hooks.eq/=->nil?)
  (is (:pass? (tc/quick-check 100 =->nil?-equivalent-property))))

(def =->empty?-equivalent-property
  (prop/for-all [x (tu/generator [:ref ::tu/seq-not-symbol])]
                (let [input (str (cons '= (shuffle `((~'count ~x) 0))))]
                  (tu/equivalent? hooks.eq/=->empty? input))))

(deftest =->empty?-test
  (tu/test-example! #'hooks.eq/=->empty?)
  (tu/test-example! #'hooks.eq/=->empty? {:in '(= (count coll) 0)})
  (is (:pass? (tc/quick-check 100 =->empty?-equivalent-property))))

(def =-remove-duplicate-equivalent-property
  (prop/for-all [x (tu/generator [:sequential {:min 2 :max 10} ::tu/not-symbol])]
                (let [input (str (cons '= (shuffle `(~(first x) ~@x))))]
                  (tu/equivalent? hooks.eq/=-remove-duplicate input))))

(deftest =-remove-duplicate-test
  (tu/test-example! #'hooks.eq/=-remove-duplicate)
  (is (:pass? (tc/quick-check 100 =-remove-duplicate-equivalent-property))))
