(ns ^:no-doc hooks.plus
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn +-remove-nested
  {:example {:in '(+ (+ x y) z)
             :out '(+ x y z)}}
  [{[_$+ & $args] :children}]
  (->> $args
       (keep (fn [{[$nested-+ & $nested-args] :children :as nested-+-node}]
               (when (and (u/list? nested-+-node)
                          (u/symbol? $nested-+ "+")
                          (<= 2 (count (:children nested-+-node))))
                 (u/reg-compression! nested-+-node $nested-+ (str/join " " $nested-args)))))
       seq))

(defn +->inc
  {:example {:in '(+ n 1)
             :out '(inc n)}}
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "1") (u/reg-compression! node $+ (str "(inc " $y ")"))
        (u/symbol? $y "1") (u/reg-compression! node $+ (str "(inc " $x ")"))))))

(defn +->dec
  {:example {:in '(+ n -1)
             :out '(dec n)}}
  [{:keys [children] :as node}]
  (let [[$+ $x $y] children]
    (when (u/count? node 3)
      (cond
        (u/symbol? $x "-1") (u/reg-compression! node $+ (str "(dec " $y ")"))
        (u/symbol? $y "-1") (u/reg-compression! node $+ (str "(dec " $x ")"))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt +-remove-nested +->inc +->dec) node)))
