(ns hooks.eq-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.eq]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest =->true?-test
  (let [code "(= 2 true)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 3
            :message "(= 2 true) -shorten-> (true? 2)"
            :type :lol}
           (hooks.eq/=->true? (api/parse-string code))))))

(deftest =->nil?-test
  (let [code "(= 2 nil)"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 3
            :message "(= 2 nil) -shorten-> (nil? 2)"
            :type :lol}
           (hooks.eq/=->nil? (api/parse-string code))))))

(deftest =->emtpy?-test
  (let [code "(= 0 (count [1]))"]
    (is (= {:row 1
            :col 2
            :end-row 1
            :end-col 3
            :message "(= 0 (count [1])) -shorten-> (empty? [1])"
            :type :lol}
           (hooks.eq/=->empty? (api/parse-string code))))
    (let [code "(= (count [1]) 0)"]
      (is (= {:row 1
              :col 2
              :end-row 1
              :end-col 3
              :message "(= (count [1]) 0) -shorten-> (empty? [1])"
              :type :lol}
             (hooks.eq/=->empty? (api/parse-string code)))))))
