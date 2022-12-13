(ns hooks.first-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.first]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest first->ffirst-test
  (let [code "(first (first [[1]]))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 7
            :message "(first (first [[1]])) -shorten-> (ffirst [[1]])"
            :type :lol}
           (hooks.first/first->ffirst (api/parse-string code))))))

(deftest first->second-test
  (let [code "(first (next [1 2]))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 7
            :message "(first (next [1 2])) -shorten-> (second [1 2])"
            :type :lol}
           (hooks.first/first->second (api/parse-string code))))))
