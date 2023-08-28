(ns ^:no-doc hooks.if
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [node]
  ;; If with a size of 3 should be `when`, which is already linted in clj-kondo.
  (u/count? node 4))

(defn if->if-not
  {:example {:in '(if (not x) y z)
             :out '(if-not x y z)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        [$test-1 $test-2] (:children $test)]
    (when (and (u/list? $test)
               (u/count? $test 2)
               (u/symbol? $test-1 "not"))
      (u/reg-compression! node $if (str "(if-not " $test-2 " " $then " " $else ")")))))

(defn if->when
  {:example {:in '(if x y nil)
             :out '(when x y)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (u/symbol? $else "nil")
      (u/reg-compression! node $if (str "(when " $test " " $then ")")))))

(defn if->boolean
  {:example {:in '(if x true false)
             :out '(boolean x)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (and (u/symbol? $then "true") (u/symbol? $else "false"))
      (u/reg-compression! node $if (str "(boolean " $test ")")))))

(defn if->not
  {:example {:in '(if t false true)
             :out '(not t)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (and (u/symbol? $then "false")
               (u/symbol? $else "true"))
      (u/reg-compression! node $if (str "(not " $test ")")))))

(defn if->cond->
  {:example {:in '(if t (f x) x)
             :out '(cond-> x t (f))}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        [$then-1 $then-2 & $then-args] (:children $then)]
    (when (and (u/list? $then)
               (<= 2 (count (:children $then)))
               (= (u/->sexpr $else) (u/->sexpr $then-2)))
      (u/reg-compression!
       node
       $if
       (str "(cond-> " $else " " $test " ("  (str/join " " (conj $then-args $then-1)) ")" ")")))))

(defn if-move-to-inner
  {:example {:in '(if t (f x y) (f z y))
             :out '(f (if t x z) y)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        $then-args (:children $then)
        $else-args (:children $else)]
    (when (and (u/list? $then)
               (u/list? $else)
               (seq $then-args)
               (= (count $then-args) (count $else-args))
               ;; Avoid moving if inside a function, since it will be evaluated for each invocation
               (->> [$then-args $else-args]
                    (map first)
                    (not-any? (some-fn #(u/symbol? % "fn") u/fn?))))
      (let [pairs (partition 2 (interleave $then-args $else-args))
            diff (keep-indexed #(when-not (u/code= (first %2) (second %2)) [%1 %2]) pairs)]
        (when (= (count diff) 1)
          (let [[[i [t e]]] diff]
            (u/reg-compression!
             node
             $if
             (str "(" (str/join " " (take i $then-args))
                  (when (pos? i) " ")
                  "(if " $test " " t " " e ") "
                  (str/join " " (drop (inc i) $then-args)) ")"))))))))

(defn if->or
  {:example {:in '(if x x y)
             :out '(or x y)}}
  [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (u/code= $test $then)
      (u/reg-compression! node $if (str "(or " $then " " $else ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt if->if-not if->when if->boolean if->not if->cond-> if-move-to-inner if->or) node)))
