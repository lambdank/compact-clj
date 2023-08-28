(ns ^:no-doc hooks.minus
  (:require
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn -->inc
  {:type :compact-clj/-->inc
   :example {:in '(- n -1)
             :out '(inc n)}}
  [{:keys [children] :as node}]
  (let [[$- $x $y] children]
    (when (and (u/count? node 3)
               (u/symbol? $y "-1"))
      (u/reg-compression! :compact-clj/-->inc node $- (str "(inc " $x ")")))))

(defn -->dec
  {:type :compact-clj/-->dec
   :example {:in '(- n 1)
             :out '(dec n)}}
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (and (u/count? node 3)
               (u/symbol? $y "1"))
      (u/reg-compression! :compact-clj/-->dec node $+ (str "(dec " $x ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt -->inc -->dec) node)))
