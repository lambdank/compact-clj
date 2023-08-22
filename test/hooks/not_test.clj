(ns hooks.not-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.not]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest not->not=-test
  (let [code "(not (= b a))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 5
            :message "(not (= b a)) -shorten-> (not= b a)"
            :type :lol}
           (hooks.not/not->not= (api/parse-string code))))))

(deftest not->not-any?-test
  (let [code "(not (some odd? [2 4 6]))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (some odd? [2 4 6])) -shorten-> (not-any? odd? [2 4 6])"
         :type :lol}
        (hooks.not/not->not-any? (api/parse-string code))))))

(deftest not->boolean-test
  (let [code "(not (not x))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (not x)) -shorten-> (boolean x)"
         :type :lol}
        (hooks.not/not->boolean (api/parse-string code))))))

(deftest not->not-every?-test
  (let [code "(not (every? odd? [1 2 3]))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (every? odd? [1 2 3])) -shorten-> (not-every? odd? [1 2 3])"
         :type :lol}
        (hooks.not/not->not-every? (api/parse-string code))))))

(deftest not->seq-test
  (let [code "(not (empty? [1]))"]
    (is
     (= (hooks.not/not->seq (api/parse-string code))
        {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (empty? [1])) -shorten-> (seq [1])"
         :type :lol}))))

(deftest not->odd?-test
  (let [code "(not (even? 1))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (even? 1)) -shorten-> (odd? 1)"
         :type :lol}
        (hooks.not/not->odd? (api/parse-string code))))))

(deftest not->even?-test
  (let [code "(not (odd? 1))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (odd? 1)) -shorten-> (even? 1)"
         :type :lol}
        (hooks.not/not->even? (api/parse-string code))))))

(deftest not->true?-test
  (let [code "(not (false? true))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (false? true)) -shorten-> (true? true)"
         :type :lol}
        (hooks.not/not->true? (api/parse-string code))))))

(deftest not->false?-test
  (let [code "(not (true? true))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (true? true)) -shorten-> (false? true)"
         :type :lol}
        (hooks.not/not->false? (api/parse-string code))))))

(deftest not->some?-test
  (let [code "(not (nil? true))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (nil? true)) -shorten-> (some? true)"
         :type :lol}
        (hooks.not/not->some? (api/parse-string code))))))

(deftest not->empty?-test
  (let [code "(not (seq [1]))"]
    (is
     (= {:row 1
         :end-row 1
         :col 2
         :end-col 5
         :message "(not (seq [1])) -shorten-> (empty? [1])"
         :type :lol}
        (hooks.not/not->empty? (api/parse-string code))))))
