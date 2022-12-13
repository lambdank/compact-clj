(ns hooks.assoc-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.assoc]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest assoc->assoc-in-test
  (let [code "(assoc {:a 1} :b (assoc (:b {:a 1}) :c 2))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 7
            :message
            (str "(assoc {:a 1} :b (assoc (:b {:a 1}) :c 2)) "
                 "-shorten-> (assoc-in {:a 1} [:b :c] 2)")
            :type :lol}
           (hooks.assoc/assoc->assoc-in (api/parse-string code))))))

(deftest assoc-remove-nested-test
  (let [code "(assoc (assoc {} :a 1) :b 2)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 7
            :message "(assoc (assoc {} :a 1) :b 2) -shorten-> (assoc {} :a 1 :b 2)"
            :type :lol}
           (hooks.assoc/assoc-remove-nested (api/parse-string code))))))

(assoc {:a 1} :b (assoc (:b {:a 1}) :c 2))
