(ns hooks.let-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.let]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest let->doto-test
  (let [code "(let [x (obj)]
                (.something x)
                (.something-else x)
                x)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 5
            :message (str "(let [x (obj)] (.something x) (.something-else x) x) "
                          "-shorten-> (doto (obj) (.something) (.something-else))")
            :type :lol}
           (hooks.let/let->doto (api/parse-string code))))))

(deftest let->when-let-test
  (let [code "(let [x (second xs)]
                (when x
                  (inc x)))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 5
            :message (str "(let [x (second xs)] (when x (inc x))) -shorten-> "
                          "(when-let [x (second xs)] (inc x))")
            :type :lol}
           (hooks.let/let->when-let (api/parse-string code))))))

(deftest let->if-let-test
  (let [code "(let [result (foo x)]
                (if result
                  (something-with result)
                  (something-else)))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 5
            :message (str "(let [result (foo x)] (if result (something-with result) (something-else)))"
                          " -shorten-> "
                          "(if-let [result (foo x)] (something-with result) (something-else))")
            :type :lol}
           (hooks.let/let->if-let (api/parse-string code))))))
