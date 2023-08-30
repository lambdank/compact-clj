(ns hooks.conj-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.conj]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest conj-remove-nested-test
  (is (= {:root-node "(conj coll x)"
          :compression "coll x"
          :type :compact-clj/conj-remove-nested}
         (-> #'hooks.conj/conj-remove-nested meta :example :in str api/parse-string hooks.conj/conj-remove-nested (dissoc :highlight-node)))))

