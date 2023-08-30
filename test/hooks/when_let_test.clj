(ns hooks.when-let-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.test-utils :as tu]
   [hooks.when-let]))

(use-fixtures :once tu/mock-reg-compression)

(def when-let->when-first-equivalent-property
  (prop/for-all [var-name (tu/generator [:ref ::tu/var])
                 coll (tu/generator [:sequential {:min 0 :max 10}
                                     [:or
                                      [:ref ::tu/not-symbol]
                                      [:ref ::tu/seq-not-symbol]]])]
                (let [x (symbol var-name)
                      input (str `(when-let [~x (~'first ~coll)]
                                    ~x))]
                  (tu/equivalent? hooks.when-let/when-let->when-first input))))

(deftest when-let->when-first-test
  (tu/test-example! #'hooks.when-let/when-let->when-first)
  (comment
    ;; This fails! (when-let [x (first [false])] x) -!> (when-first [x [false])] x)
    (is (:pass? (tc/quick-check 100 when-let->when-first-equivalent-property)))))
