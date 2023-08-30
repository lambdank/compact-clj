(ns hooks.assoc-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.assoc]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest assoc-remove-nested-test
  (is (= {:root-node "(assoc m :a x)"
          :compression "m :a x"
          :type :compact-clj/assoc-remove-nested}
         (-> #'hooks.assoc/assoc-remove-nested meta :example :in str api/parse-string hooks.assoc/assoc-remove-nested (dissoc :highlight-node)))))

(deftest assoc->assoc-in-test
  (tu/test-example! #'hooks.assoc/assoc->assoc-in)
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:in '(assoc m :a (assoc (m :a) :b x))})
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:in '(assoc m :a (assoc (get m :a) :b x))}))

(deftest assoc->update-test
  (tu/test-example! #'hooks.assoc/assoc->update)
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (get m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (m k) x y z))
                                                 :out '(update m k (f x y z))}))
