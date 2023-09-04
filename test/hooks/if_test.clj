(ns hooks.if-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.if]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def if->if-not-equivalent-property
  (prop/for-all [coll (tu/generator [:sequential {:min 2 :max 2}
                                     ::tu/seq-not-symbol])
                 bool (tu/generator :boolean)]
                (let [input (str `(~'if (~'not ~bool) ~@coll))]
                  (tu/equivalent? hooks.if/if->if-not input))))

(deftest if->if-not-test
  (tu/test-example! #'hooks.if/if->if-not)
  (is (:pass? (tc/quick-check 100 if->if-not-equivalent-property))))

(def if->when-equivalent-property
  (prop/for-all [x (tu/generator ::tu/seq-not-symbol)
                 bool (tu/generator :boolean)]
                (let [input (str `(~'if ~bool ~x ~'nil))]
                  (tu/equivalent? hooks.if/if->when input))))

(deftest if->when-test
  (tu/test-example! #'hooks.if/if->when)
  (is (:pass? (tc/quick-check 100 if->when-equivalent-property))))

(def if->boolean-equivalent-property
  (prop/for-all [x (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'if ~x ~'true ~'false))]
                  (tu/equivalent? hooks.if/if->boolean input))))

(deftest if->boolean-test
  (tu/test-example! #'hooks.if/if->boolean)
  (is (:pass? (tc/quick-check 100 if->boolean-equivalent-property))))

(def if->not-equivalent-property
  (prop/for-all [x (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'if ~x ~'false ~'true))]
                  (tu/equivalent? hooks.if/if->not input))))

(deftest if->not-test
  (tu/test-example! #'hooks.if/if->not)
  (is (:pass? (tc/quick-check 100 if->not-equivalent-property))))

(def if->cond->-equivalent-property
  (prop/for-all [x (tu/generator ::tu/seq-not-symbol)
                 bool (tu/generator :boolean)
                 f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])]
                (let [input (str `(~'if ~bool (~f ~x) ~x))]
                  (tu/equivalent? hooks.if/if->cond-> input))))

(deftest if->cond->-test
  (tu/test-example! #'hooks.if/if->not)
  (is (nil? (hooks.if/if->cond-> (api/parse-string "(if (= 2 2) (f) nil)"))))
  (is (:pass? (tc/quick-check 100 if->cond->-equivalent-property))))

(def if->or-equivalent-property
  (prop/for-all [x (tu/generator ::tu/seq-not-symbol)
                 y (tu/generator ::tu/seq-not-symbol)]
                (let [input (str `(~'if ~x ~x ~y))]
                  (tu/equivalent? hooks.if/if->or input))))

(deftest if->or
  (tu/test-example! #'hooks.if/if->or)
  (is (:pass? (tc/quick-check 100 if->or-equivalent-property))))

(deftest if-move-to-inner
  (tu/test-example! #'hooks.if/if-move-to-inner)
  (is (nil? (hooks.if/if-move-to-inner (api/parse-string "(if (= a b) (fn x a y) (fn x b y))"))))
  (is (nil? (hooks.if/if-move-to-inner (api/parse-string "(if (= a b) (fn x a y) (fn x b y))")))))
