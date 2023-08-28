(ns ^:no-doc hooks.plus
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn +-remove-nested
  "Compression: (+ (+ x y) z) -> (+ x y z)"
  [{[_$+ & $args] :children}]
  (->> $args
       (keep (fn [{[$nested-+ & $nested-args] :children :as nested-+-node}]
               (when (and (u/list? nested-+-node)
                          (u/symbol? $nested-+ "+")
                          (<= 2 (count (:children nested-+-node))))
                 (u/reg-compression! nested-+-node $nested-+ (str/join " " $nested-args)))))
       doall))

(defn +->inc
  "Compression: (+ n 1) -> (inc n)"
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "1") (u/reg-compression! node $+ (str "(inc " $y ")"))
        (u/symbol? $y "1") (u/reg-compression! node $+ (str "(inc " $x ")"))))))

(defn +->dec
  "Compression: (+ n -1) -> (dec n)"
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "-1") (u/reg-compression! node $+ (str "(dec " $y ")"))
        (u/symbol? $y "-1") (u/reg-compression! node $+ (str "(dec " $x ")"))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt +-remove-nested +->inc +->dec) node)))
