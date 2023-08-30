(ns hooks.and-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.and]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest and-remove-nested-test
  (is (= (list {:root-node "(and x y)"
                :compression "x y"
                :type :compact-clj/and-remove-nested})
         (->> (-> #'hooks.and/and-remove-nested meta :example :in str api/parse-string hooks.and/and-remove-nested)
              (map #(dissoc % :highlight-node))))))

(deftest and->every?
  (tu/test-example! #'hooks.and/and->every?))
