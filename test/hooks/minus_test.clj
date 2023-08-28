(ns hooks.minus-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.minus]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest -->inc-test
  (let [code "(- n -1)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 3
            :message "(- n -1) -shorten-> (inc n)"
            :type :lol}
           (hooks.minus/-->inc (api/parse-string code))))))

(deftest -->dec
  (let [code "(- n 1)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 3
            :message "(- n 1) -shorten-> (dec n)"
            :type :lol}
           (hooks.minus/-->dec (api/parse-string code))))))
