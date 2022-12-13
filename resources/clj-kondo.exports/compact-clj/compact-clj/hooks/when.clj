(ns ^:no-doc hooks.when
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn when->when-not [node]
  (let [{[$when $test & $body] :children} node
        {[$test-1 $test-2] :children} $test]
    (when (u/symbol? $test-1 "not")
      (api/reg-finding!
       (assoc (meta $when)
              :message (u/->msg node (str "(when-not " $test-2 " " (str/join " " $body) ")"))
              :type :lol)))))

(defn when->not-empty [node]
  (let [{[$when $test & $body] :children} node
        {[$test-1 $test-2] :children} $test]
    (when (and (= (count $body) 1)
               (u/symbol? $test-1 "seq")
               (= (u/->sexpr $test-2) (u/->sexpr (first $body))))
      (api/reg-finding!
       (assoc (meta $when)
              :message (u/->msg node (str "(not-empty " $test-2")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt when->not-empty when->when-not) node)))
