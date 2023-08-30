(ns hooks.plus-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.plus]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest +-remove-nested-test
  (is (= (list {:root-node "(+ x y)"
                :compression "x y"
                :type :compact-clj/+-remove-nested})
         (->> (-> #'hooks.plus/+-remove-nested meta :example :in str api/parse-string hooks.plus/+-remove-nested)
              (map #(dissoc % :highlight-node))))))

(deftest +->inc-test
  (tu/test-example! #'hooks.plus/+->inc)
  (tu/test-example! #'hooks.plus/+->inc {:in '(+ 1 n)}))

(deftest +->dec-test
  (tu/test-example! #'hooks.plus/+->dec)
  (tu/test-example! #'hooks.plus/+->dec {:in '(+ -1 n)}))
