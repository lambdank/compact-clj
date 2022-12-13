(ns hooks.conj-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.conj]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest when-let->when-first
  (let [code "(conj (conj [] x) y)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 6
            :message "(conj (conj [] x) y) -shorten-> (conj [] x y)"
            :type :lol}
           (hooks.conj/conj-remove-nested (api/parse-string code))))))
