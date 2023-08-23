(ns ^:no-doc hooks.or
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ;; `or` with one argument is already linted by clj-kondo.
  (<= 2 (count children)))

(defn or->some
  "Compression: (or (f x) (f y)) -> (some f [x y])"
  [{:keys [children] :as node}]
  (let [[$or & $args] children
        {[$pred] :children} (first $args)]
    (when (and (every? #(and (u/list? %) (u/count? % 2)) $args)
               (every? (fn [{[pred] :children}] (= pred $pred)) (rest $args)))
      (u/reg-compression!
       node
       $or
       (str "(some " $pred " [" (str/join " " (map (comp second :children) $args)) "])")))))

(defn or->get
  "Compression: ((or (get m k) x) -> (get m k x)"
  [{:keys [children] :as node}]
  (let [[$or $x $y] children]
    (when (and (u/count? node 3)
               (u/list? $x))
      (cond (and (u/keyword? (first (:children $x)))
                 (u/count? $x 2))
            (let [[$kw $m] (:children $x)]
              (u/reg-compression! node $or (str "(" $kw " " $m " " $y ")")))

            (and (u/symbol? (first (:children $x)) "get")
                 (u/count? $x 3))
            (let [[_$get $m $k] (:children $x)]
              (u/reg-compression! node $or (str "(get " $m " " $k " " $y ")")))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt or->some or->get) node)))
