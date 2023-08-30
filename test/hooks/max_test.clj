(ns hooks.max-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.max]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest max-remove-nested-test
  (is (= (list {:root-node "(max x y)"
                :compression "x y"
                :type :compact-clj/max-remove-nested})
         (->> (-> #'hooks.max/max-remove-nested meta :example :in str api/parse-string hooks.max/max-remove-nested)
              (map #(dissoc % :highlight-node))))))
