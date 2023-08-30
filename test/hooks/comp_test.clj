(ns hooks.comp-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.comp]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest comp-remove-nested-test
  (is (= (list {:root-node "(comp f g)"
                :compression "f g"
                :type :compact-clj/comp-remove-nested})
         (->> (-> #'hooks.comp/comp-remove-nested meta :example :in str api/parse-string hooks.comp/comp-remove-nested)
              (map #(dissoc % :highlight-node))))))
