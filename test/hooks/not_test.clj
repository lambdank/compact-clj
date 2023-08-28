(ns hooks.not-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.not]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest not->not=-test
  (tu/test-example! #'hooks.not/not->not= {:col 2 :end-col 5}))

(deftest not->not-any?-test
  (tu/test-example! #'hooks.not/not->not-any? {:col 2 :end-col 5}))

(deftest not->boolean-test
  (tu/test-example! #'hooks.not/not->boolean {:col 2 :end-col 5}))

(deftest not->not-every?-test
  (tu/test-example! #'hooks.not/not->not-every? {:col 2 :end-col 5}))

(deftest not->seq-test
  (tu/test-example! #'hooks.not/not->seq {:col 2 :end-col 5}))

(deftest not->odd?-test
  (tu/test-example! #'hooks.not/not->odd? {:col 2 :end-col 5}))

(deftest not->even?-test
  (tu/test-example! #'hooks.not/not->even? {:col 2 :end-col 5}))

(deftest not->true?-test
  (tu/test-example! #'hooks.not/not->true? {:col 2 :end-col 5}))

(deftest not->false?-test
  (tu/test-example! #'hooks.not/not->false? {:col 2 :end-col 5}))

(deftest not->some?-test
  (tu/test-example! #'hooks.not/not->some? {:col 2 :end-col 5}))

(deftest not->empty?-test
  (tu/test-example! #'hooks.not/not->empty? {:col 2 :end-col 5}))
