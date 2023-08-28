(ns hooks.plus-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.plus]
   [hooks.test-utils :as tu]
   [hooks.utils :as u]))

(use-fixtures :once tu/mock-reg-finding)

(deftest +-remove-nested-test
  (is (= (list {:row 1
                :end-row 1
                :col 5
                :end-col 6
                :message (u/->msg "(+ x y)" "x y")
                :type :lol})
         (hooks.plus/+-remove-nested (api/parse-string (-> #'hooks.plus/+-remove-nested meta :example :in str))))))

(deftest +->inc-test
  (tu/test-example! #'hooks.plus/+->inc {:col 2 :end-col 3})
  (tu/test-example! #'hooks.plus/+->inc {:col 2 :end-col 3
                                         :in '(+ 1 n)}))

(deftest +->dec-test
  (tu/test-example! #'hooks.plus/+->dec {:col 2 :end-col 3})
  (tu/test-example! #'hooks.plus/+->dec {:col 2 :end-col 3
                                         :in '(+ -1 n)}))
