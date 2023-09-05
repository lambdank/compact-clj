(ns hooks.get-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.get]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def get->kw-equivalent-property
  (prop/for-all [k (tu/generator :keyword)
                 m (tu/generator :map)
                 in-map (tu/generator :boolean)
                 found (tu/generator ::tu/not-symbol)
                 not-found (tu/generator ::tu/not-symbol)]
                (let [modified-m (cond-> m in-map (assoc k found))
                      input (str `(get ~modified-m ~k ~not-found))]
                  (tu/equivalent? hooks.get/get->kw input))))

(deftest get->kw-test
  (tu/test-example! #'hooks.get/get->kw)
  (is (:pass? (tc/quick-check 100 get->kw-equivalent-property))))
