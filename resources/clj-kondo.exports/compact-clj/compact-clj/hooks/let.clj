(ns ^:no-doc hooks.let
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn legal? [{:keys [children]}]
  (let [[_$let $bindings & $exprs] children]
    (and (seq $exprs) (u/vector? $bindings))))

(defn let->doto
  {:example {:in '(let [x y] (f x) (g x) x)
             :out '(doto y (f) (g))}}
  [{:keys [children] :as node}]
  (let [[$let $bindings & $exprs] children
        [$key $value] (:children $bindings)
        exprs-wihout-last (drop-last $exprs)]
    (when (and (u/count? $bindings 2)
               (= (last $exprs) $key)
               (every? #(= (second (:children %)) $key) exprs-wihout-last))
      (let [modified-exprs (map #(update % :children (fn [[$f _$key & $args]]
                                                       (into (list $f) $args)))
                                exprs-wihout-last)]
        (u/reg-compression! node $let (str "(doto " $value " " (str/join " " modified-exprs) ")"))))))

(defn let->when-let
  {:example {:in '(let [x y] (when x (f x)))
             :out '(when-let [x y] (f x))}}
  [{:keys [children] :as node}]
  (let [[$let $bindings $exprs] children
        [$key] (:children $bindings)
        [$when $test & $body] (:children $exprs)]
    (when (and (u/count? node 3)
               (u/count? $bindings 2)
               (u/list? $exprs)
               (u/symbol? $when "when")
               (= $key $test))
      (u/reg-compression! node $let (str "(when-let " $bindings " " (str/join " " $body) ")")))))

(defn let->if-let
  {:example {:in '(let [x y] (if x (f x) z))
             :out '(if-let [x y] (f x) z)}}
  [{:keys [children] :as node}]
  (let [[$let $bindings $exprs] children
        [$key] (:children $bindings)
        [$if $predicate $then $else] (:children $exprs)]
    (when (and (u/count? node 3)
               (u/count? $bindings 2)
               (u/list? $exprs)
               (u/count? $exprs 4)
               (= $key $predicate)
               (u/symbol? $if "if")
               (not-any? #{$predicate} (:children $else)))
      (u/reg-compression! node $let (str "(if-let " $bindings " " $then " " $else ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt let->doto let->when-let) node)))
