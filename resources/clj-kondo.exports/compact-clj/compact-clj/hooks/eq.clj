(ns ^:no-doc hooks.eq
  (:require
   [hooks.utils :as u]))

(defn- legal? [node]
  (pos? (count (:children node))))

(defn =->true?
  "Compression: (= x true) -> (true? x)"
  [{:keys [children] :as node}]
  (let [[$= $x $y] children]
    (when (u/count? node 3)
      (cond (u/symbol? $x "true") (u/reg-compression! node $= (str "(true? " $y ")"))
            (u/symbol? $y "true") (u/reg-compression! node $= (str "(true? " $x ")"))))))

(defn =->nil?
  "Compression: (= x nil) -> (nil? x)"
  [{:keys [children] :as node}]
  (let [[$= $x $y] children]
    (when (u/count? node 3)
      (cond (u/symbol? $x "nil") (u/reg-compression! node $= (str "(nil? " $y ")"))
            (u/symbol? $y "nil") (u/reg-compression! node $= (str "(nil? " $x ")"))))))

(defn =->empty?
  "Compression: (= 0 (count coll)) -> (empty? coll)"
  [{:keys [children] :as node}]
  (let [[$= $x $y] children
        [$x-count $x-coll] (:children $x)
        [$y-count $y-coll] (:children $y)]
    (when (u/count? node 3)
      (cond (and (u/symbol? $x "0")
                 (u/list? $y)
                 (u/symbol? $y-count "count"))
            (u/reg-compression! node $= (str "(empty? " $y-coll ")"))

            (and (u/symbol? $y "0")
                 (u/list? $x)
                 (u/symbol? $x-count "count"))
            (u/reg-compression! node $= (str "(empty? " $x-coll ")"))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt =->true? =->nil? =->empty?) node)))
