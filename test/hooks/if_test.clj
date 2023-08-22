(ns hooks.if-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.if]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest if->if-not-test
  (let [code "(if (not (= b a)) c d)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(if (not (= b a)) c d) -shorten-> (if-not (= b a) c d)"
            :type :lol}
           (hooks.if/if->if-not (api/parse-string code))))))

(deftest if->when-test
  (let [code "(if (p x) y nil)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(if (p x) y nil) -shorten-> (when (p x) y)"
            :type :lol}
           (hooks.if/if->when (api/parse-string code))))))

(deftest if->boolean
  (let [code "(if (p x) true false)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(if (p x) true false) -shorten-> (boolean (p x))"
            :type :lol}
           (hooks.if/if->boolean (api/parse-string code))))))

(deftest if->cond->test
  (let [code "(if (= a b) (inc a) a)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(if (= a b) (inc a) a) -shorten-> (cond-> a (= a b) (inc))"
            :type :lol}
           (hooks.if/if->cond-> (api/parse-string code)))))
  (let [code "(if (= 2 2) (f) nil)"]
    (is (nil? (hooks.if/if->cond-> (api/parse-string code))))))

(deftest if-move-to-inner
  (let [code "(if (= a b) (f x a y) (f x b y))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(if (= a b) (f x a y) (f x b y)) -shorten-> (f x (if (= a b) a b) y)"
            :type :lol}
           (hooks.if/if-move-to-inner (api/parse-string code))))))
