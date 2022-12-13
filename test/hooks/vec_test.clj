(ns hooks.vec-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.test-utils :refer [mock-reg-finding]]
   [hooks.vec]))

(use-fixtures :once mock-reg-finding)

(deftest vec->mapv-test
  (let [code "(vec (map + [1 2 3] [4 5 6]))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 5
            :message "(vec (map + [1 2 3] [4 5 6])) -shorten-> (mapv + [1 2 3] [4 5 6])"
            :type :lol}
           (hooks.vec/vec->mapv (api/parse-string code))))))
