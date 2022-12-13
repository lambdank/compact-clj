(ns hooks.when-let-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.test-utils :refer [mock-reg-finding]]
   [hooks.when-let]))

(use-fixtures :once mock-reg-finding)

(deftest when-let->when-first
  (let [code "(when-let [x (first xs)] (f x))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 10
            :message "(when-let [x (first xs)] (f x)) -shorten-> (when-first [x xs] (f x))"
            :type :lol}
           (hooks.when-let/when-let->when-first (api/parse-string code))))))
