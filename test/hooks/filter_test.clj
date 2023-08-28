(ns hooks.filter-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [hooks.filter]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-finding)

(deftest filter->remove-test
  (tu/test-example! #'hooks.filter/filter->remove {:col 2 :end-col 8})
  (tu/test-example! #'hooks.filter/filter->remove {:col 2 :end-col 8
                                                   :in "(filter #(not (f %)) coll)"
                                                   :out "(remove #(f %) coll)"})
  (tu/test-example! #'hooks.filter/filter->remove {:col 2 :end-col 8
                                                   :in '(filter (complement f) coll)
                                                   :out '(remove f coll)}))

(deftest filter->keep-test
  (tu/test-example! #'hooks.filter/filter->keep {:col 2 :end-col 8}))
