(ns hooks.plus-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.plus]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest +-remove-nested-test
  (is (= (list {:root-node "(+ x y)"
                :compression "x y"
                :type :compact-clj/+-remove-nested})
         (->> (-> #'hooks.plus/+-remove-nested meta :example :in str api/parse-string hooks.plus/+-remove-nested)
              (map #(dissoc % :highlight-node))))))

(def +->inc-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:int]
                                  [:double {:gen/NaN? false}]])]
                (let [input (str `(+ ~x 1))]
                  (tu/equivalent? hooks.plus/+->inc input))))

(deftest +->inc-test
  (tu/test-example! #'hooks.plus/+->inc)
  (tu/test-example! #'hooks.plus/+->inc {:in '(+ 1 n)})
  (is (:pass? (tc/quick-check 100 +->inc-equivalent-property))))

(def +->dec-equivalent-property
  (prop/for-all [x (tu/generator [:or
                                  [:int]
                                  [:double {:gen/NaN? false}]])]
                (let [input (str `(+ ~x -1))]
                  (tu/equivalent? hooks.plus/+->dec input))))

(deftest +->dec-test
  (tu/test-example! #'hooks.plus/+->dec)
  (tu/test-example! #'hooks.plus/+->dec {:in '(+ -1 n)})
  (is (:pass? (tc/quick-check 100 +->dec-equivalent-property))))
