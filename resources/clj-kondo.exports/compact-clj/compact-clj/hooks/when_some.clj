(ns hooks.when-some
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (let [[_$when-some $bindings & $body] children]
    (and (seq $body) (u/vector? $bindings) (u/count? $bindings 2))))

(defn when-some->some->
  {:type :compact-clj/when-some->some->
   :example {:in '(when-some [x y] (f x))
             :out '(some-> y f)}}
  [{:keys [children] :as node}]
  (let [[$when-let $bindings $body] children
        [$key $value] (:children $bindings)
        [$f $x & $args] (:children $body)]
    (when (and (u/count? node 3)
               (u/count? $bindings 2)
               (u/vector? $bindings)
               (u/list? $body)
               (u/count? $body 2)
               (u/code= $x $key))
      (u/reg-compression!
       :compact-clj/when-some->some->
       node
       $when-let
       (str "(some-> " $value " "
            (if (seq $args)
              (str "(" $f " " (str/join " " $args) ")")
              $f) ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (when-some->some-> node)))
