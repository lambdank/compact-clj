(ns hooks.min-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.min]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest min-remove-nested-test
  (is (= (list {:root-node "(min x y)"
                :compression "x y"
                :type :compact-clj/min-remove-nested})
         (->> (-> #'hooks.min/min-remove-nested meta :example :in str api/parse-string hooks.min/min-remove-nested)
              (map #(dissoc % :highlight-node))))))
