(ns ^:no-doc hooks.when
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn when->when-not
  {:example {:in '(when (not x) body)
             :out '(when-not x body)}}
  [{:keys [children] :as node}]
  (let [[$when $test & $body] children
        [$not $x] (:children $test)]
    (when (and (u/list? $test)
               (u/symbol? $not "not")
               (u/count? $test 2))
      (u/reg-compression! node $when (str "(when-not " $x " " (str/join " " $body) ")")))))

(defn when->not-empty
  {:example {:in '(when (seq coll) coll)
             :out '(not-empty coll)}}
  [{:keys [children] :as node}]
  (let [[$when $test & $body] children
        [$seq $coll] (:children $test)]
    (when (and (= (count $body) 1)
               (u/list? $test)
               (u/symbol? $seq "seq")
               (u/count? $test 2)
               (u/code= (first $body) $coll))
      (u/reg-compression! node $when (str "(not-empty " $coll")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt when->not-empty when->when-not) node)))
