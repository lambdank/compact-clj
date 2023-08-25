(ns ^:no-doc hooks.first
  (:require
   [hooks.utils :as u]))

(defn- legal? [node]
  (u/count? node 2))

(defn first->ffirst
  "Compression: (first (first coll)) -> (ffirst coll)"
  [{:keys [children] :as node}]
  (let [[$first {[$nested-first $nested-coll] :children :as $coll}] children]
    (when (and (u/count? node 2)
               (u/list? $coll)
               (u/symbol? $nested-first "first")
               (u/count? $coll 2))
      (u/reg-compression! node $first (str "(ffirst " $nested-coll ")")))))

(defn first->second
  "Compression: (first (next coll)) -> (second coll)"
  [{:keys [children] :as node}]
  (let [[$first {[$next $nested-coll] :children :as $coll}] children]
    (when (and (u/count? node 2)
               (u/list? $coll)
               (u/symbol? $next "next")
               (u/count? $coll 2))
      (u/reg-compression! node $first (str "(second " $nested-coll  ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt first->ffirst first->second) node)))

