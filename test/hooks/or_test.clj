(ns hooks.or-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.or]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest or->some-test
  (let [code "(or (even? 5) (even? 7) (even? 8))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(or (even? 5) (even? 7) (even? 8)) -shorten-> (some even? [5 7 8])"
            :type :lol}
           (hooks.or/or->some (api/parse-string code))))))

(deftest or->get-test
  (let [code "(or (:a x) y)"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 4
            :message "(:a x y)"
            :type :lol}
           (hooks.or/or->get (api/parse-string code))))))
