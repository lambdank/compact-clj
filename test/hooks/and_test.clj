(ns hooks.and-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.and]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest and-remove-nested
  (let [code "(and true (and 1 2))"]
    (is (= (list {:row 1
                  :end-row 1
                  :col 12
                  :end-col 15
                  :message "(and 1 2) -shorten-> 1 2"
                  :type :lol})
           (hooks.and/and-remove-nested (api/parse-string code))))))

(deftest and->every?
  (let [code "(and (even? 6) (even? 8))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 5
            :message "(and (even? 6) (even? 8)) -shorten-> (every? even? [6 8])"
            :type :lol}
           (hooks.and/and->every? (api/parse-string code))))))
