(ns hooks.plus-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.plus]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest +-remove-nested-test
  (let [code "(+ (+ x y) z)"]
    (is (= (list {:row 1
                  :end-row 1
                  :col 5
                  :end-col 6
                  :message "(+ x y) -shorten-> x y"
                  :type :lol})
           (hooks.plus/+-remove-nested (api/parse-string code))))))

(deftest +->inc-test
  (let [code "(+ n 1)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 3
            :message "(+ n 1) -shorten-> (inc n)"
            :type :lol}
           (hooks.plus/+->inc (api/parse-string code))))))

(deftest +->dec-test
  (let [code "(+ n -1)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 3
            :message "(+ n -1) -shorten-> (dec n)"
            :type :lol}
           (hooks.plus/+->dec (api/parse-string code))))))
