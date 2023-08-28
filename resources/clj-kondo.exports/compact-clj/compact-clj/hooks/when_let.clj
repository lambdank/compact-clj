(ns ^:no-doc hooks.when-let
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (let [[_$let $bindings & $body] children]
    (and (seq $body) (u/vector? $bindings) (even? (count (:children $bindings))))))

(defn when-let->when-first
  {:example {:in '(when-let [x (first xs)] (f x))
             :out '(when-first [x xs] (f x))}}
  [{:keys [children] :as node}]
  (let [[$when-let $bindings & $body] children
        {[$key $value] :children} $bindings
        {[$first $coll] :children} $value]
    (when (and (u/count? $bindings 2)
               (u/vector? $bindings)
               (u/list? $value)
               (u/count? $value 2)
               (u/symbol? $first "first"))
      (u/reg-compression!
       node
       $when-let
       (str "(when-first [" $key " " $coll "] " (str/join " " $body)")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (when-let->when-first node)))
