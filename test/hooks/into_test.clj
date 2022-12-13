(ns hooks.into-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.into]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest into->set-test
  (let [code "(into #{} [1 2 3])"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 6
            :message "(into #{} [1 2 3]) -shorten-> (set [1 2 3])"
            :type :lol}
           (hooks.into/into->set (api/parse-string code))))))

(deftest into->vec-test
  (let [code "(into [] [1 2 3])"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 6
            :message "(into [] [1 2 3]) -shorten-> (vec [1 2 3])"
            :type :lol}
           (hooks.into/into->vec (api/parse-string code))))))
