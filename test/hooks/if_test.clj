(ns hooks.if-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.if]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(deftest if->if-not-test
  (tu/test-example! #'hooks.if/if->if-not))

(deftest if->when-test
  (tu/test-example! #'hooks.if/if->when))

(deftest if->boolean-test
  (tu/test-example! #'hooks.if/if->boolean))

(deftest if->not-test
  (tu/test-example! #'hooks.if/if->not))

(deftest if->cond->test
  (tu/test-example! #'hooks.if/if->not)
  (is (nil? (hooks.if/if->cond-> (api/parse-string "(if (= 2 2) (f) nil)")))))

(deftest if-move-to-inner
  (tu/test-example! #'hooks.if/if-move-to-inner)
  (is (nil? (hooks.if/if-move-to-inner (api/parse-string "(if (= a b) (fn x a y) (fn x b y))"))))
  (is (nil? (hooks.if/if-move-to-inner (api/parse-string "(if (= a b) (fn x a y) (fn x b y))")))))

(deftest if->or
  (tu/test-example! #'hooks.if/if->or))
