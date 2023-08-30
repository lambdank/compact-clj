(ns hooks.or-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.or]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest or-remove-nested-test
  (is (= (list {:root-node "(or x y)"
                :compression "x y"
                :type :compact-clj/or-remove-nested})
         (->> (-> #'hooks.or/or-remove-nested meta :example :in str api/parse-string hooks.or/or-remove-nested)
              (map #(dissoc % :highlight-node))))))

(deftest or->some-test
  (tu/test-example! #'hooks.or/or->some))

(deftest or->get-test
  (tu/test-example! #'hooks.or/or->get)
  (tu/test-example! #'hooks.or/or->get {:in '(or (get m k) x)}))
