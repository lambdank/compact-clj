(ns ^:no-doc hooks.plus
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn +-remove-nested
  {:type :compact-clj/+-remove-nested
   :example {:in '(+ (+ x y) z)
             :out '(+ x y z)}}
  [{[_$+ & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "+"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/+-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn +->inc
  {:type :compact-clj/+->inc
   :example {:in '(+ n 1)
             :out '(inc n)}}
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "1") (u/reg-compression! :compact-clj/+->inc node $+ (str "(inc " $y ")"))
        (u/symbol? $y "1") (u/reg-compression! :compact-clj/+->inc node $+ (str "(inc " $x ")"))))))

(defn +->dec
  {:type :compact-clj/+->dec
   :example {:in '(+ n -1)
             :out '(dec n)}}
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "-1") (u/reg-compression! :compact-clj/+->dec node $+ (str "(dec " $y ")"))
        (u/symbol? $y "-1") (u/reg-compression! :compact-clj/+->dec node $+ (str "(dec " $x ")"))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt +-remove-nested +->inc +->dec) node)))
