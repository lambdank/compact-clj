(ns hooks.or-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.or]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest or-remove-nested-test
  (is (= (list {:root-node "(or x y)"
                :compression "x y"
                :type :compact-clj/or-remove-nested})
         (->> (-> #'hooks.or/or-remove-nested meta :example :in str api/parse-string hooks.or/or-remove-nested)
              (map #(dissoc % :highlight-node))))))

(def or->some-equivalent-property
  (prop/for-all [f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])
                 coll (tu/generator [:sequential {:min 2 :max 10}
                                     ::tu/not-symbol])]
                (let [xs (map (fn [x] `(~f ~x)) coll)
                      input (str `(~'or ~@xs))]
                  (tu/equivalent? hooks.or/or->some input
                                  {:in-post #(if (false? %) nil %)}))))

(deftest or->some-test
  (tu/test-example! #'hooks.or/or->some)
  (is (:pass? (tc/quick-check 100 or->some-equivalent-property))))

(deftest or->get-test
  (tu/test-example! #'hooks.or/or->get)
  (tu/test-example! #'hooks.or/or->get {:in '(or (get m k) x)}))
