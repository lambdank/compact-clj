(ns hooks.filter-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [deftest use-fixtures is]]
   [hooks.filter]
   [hooks.test-utils :refer [mock-reg-finding]]))

(use-fixtures :once mock-reg-finding)

(deftest filter->remove-test
  (let [code "(filter (complement odd?) [1 2 3])"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 8
            :message "(filter (complement odd?) [1 2 3]) -shorten-> (remove odd? [1 2 3])"
            :type :lol}
           (hooks.filter/filter->remove (api/parse-string code)))))
  (let [code "(filter (fn [x] (not (odd? x))) [1 2 3])"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 8
            :message "(filter (fn [x] (not (odd? x))) [1 2 3]) -shorten-> (remove (fn [x] (odd? x)) [1 2 3])"
            :type :lol}
           (hooks.filter/filter->remove (api/parse-string code)))))
  (let [code "(filter #(not (odd? %)) [1 2 3])"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 8
            :message "(filter #(not (odd? %)) [1 2 3]) -shorten-> (remove #(odd? %) [1 2 3])"
            :type :lol}
           (hooks.filter/filter->remove (api/parse-string code))))))

(comment (api/parse-string "(filter (fn [x] (not (odd?))) [1 2 3])"))

(deftest filter->keep-test
  (let [code "(filter some? (map (fn [x] (some-> x #{1 2} inc)) [1 2 3]))"]
    (is (= {:row 1
            :end-row 1
            :col 2
            :end-col 8
            :message (str "(filter some? (map (fn [x] (some-> x #{1 2} inc)) [1 2 3]))"
                          " -shorten-> (keep (fn [x] (some-> x #{1 2} inc)) [1 2 3])")
            :type :lol}
           (hooks.filter/filter->keep (api/parse-string code))))))
