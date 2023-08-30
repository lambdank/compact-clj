(ns hooks.eq-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.eq]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest =-remove-nested-test
  (is (= (list {:root-node "(= x y)"
                :compression "x y"
                :type :compact-clj/=-remove-nested})
         (->> (hooks.eq/=-remove-nested (api/parse-string (-> #'hooks.eq/=-remove-nested meta :example :in str)))
              (map #(dissoc % :highlight-node))))))

(deftest =->true?-test
  (tu/test-example! #'hooks.eq/=->true?))

(deftest =->nil?-test
  (tu/test-example! #'hooks.eq/=->nil?))

(deftest =->emtpy?-test
  (tu/test-example! #'hooks.eq/=->empty?)
  (tu/test-example! #'hooks.eq/=->empty? {:in '(= (count coll) 0)}))

(deftest =-remove-duplicate-test
  (tu/test-example! #'hooks.eq/=-remove-duplicate))
