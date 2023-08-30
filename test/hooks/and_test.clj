(ns hooks.and-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.and]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest and-remove-nested-test
  (is (= (list {:root-node "(and x y)"
                :compression "x y"
                :type :compact-clj/and-remove-nested})
         (->> (-> #'hooks.and/and-remove-nested meta :example :in str api/parse-string hooks.and/and-remove-nested)
              (map #(dissoc % :highlight-node))))))

(def and->every?-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 x (tu/generator [:sequential {:min 2 :max 10} ::tu/not-symbol])]
                (let [input (str (cons 'and (map (fn [y] `(~f ~y)) x)))]
                  (tu/equivalent? hooks.and/and->every? input {:f-in boolean}))))

(deftest and->every?
  (tu/test-example! #'hooks.and/and->every?)
  (is (:pass? (tc/quick-check 100 and->every?-equivalent-property))))
