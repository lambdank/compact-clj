(ns hooks.map-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.map]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest map-remove-nested-test
  (let [code "(map inc (map dec [1 2 3]))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 5
            :message "(map inc (map dec [1 2 3])) -shorten-> (map (comp inc dec) [1 2 3])"
            :type :lol}
           (hooks.map/map-remove-nested (api/parse-string code))))))
