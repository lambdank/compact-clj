(ns hooks.when-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.test-utils :refer [mock-reg-finding]]
   [hooks.when]))

(use-fixtures :once mock-reg-finding)

(deftest when->when-not-test
  (let [code "(when (not true) 2)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 6
            :message "(when (not true) 2) -shorten-> (when-not true 2)"
            :type :lol}
           (hooks.when/when->when-not (api/parse-string code))))))

(deftest when->not-empty-test
  (let [code "(when (seq a) a)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 6
            :message "(when (seq a) a) -shorten-> (not-empty a)"
            :type :lol}
           (hooks.when/when->not-empty (api/parse-string code))))))
