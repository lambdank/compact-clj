(ns ^:no-doc hooks.or
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ;; `or` with one argument is already linted by clj-kondo.
  (<= 2 (count children)))

(defn or-remove-nested
  {:type :compact-clj/or-remove-nested
   :example {:in '(or (or x y) z)
             :out '(or x y z)}}
  [{[_$and & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "or"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/or-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn or->some
  {:type :compact-clj/or->some
   :example {:in '(or (f x) (f y))
             :out '(some f [x y])}}
  [{:keys [children] :as node}]
  (let [[$or & $args] children
        {[$pred] :children} (first $args)]
    (when (and (every? #(and (u/list? %) (u/count? % 2)) $args)
               (every? (fn [{[pred] :children}] (= pred $pred)) (rest $args)))
      (u/reg-compression!
       :compact-clj/or->some
       node
       $or
       (str "(some " $pred " [" (str/join " " (map (comp second :children) $args)) "])")))))

(defn or->get
  {:type :compact-clj/or->get
   :example {:in '(or (get m k) x)
             :out '(get m k x)}}
  [{:keys [children] :as node}]
  (let [[$or $x $y] children]
    (when (and (u/count? node 3)
               (u/list? $x))
      (cond (and (u/keyword? (first (:children $x)))
                 (u/count? $x 2))
            (let [[$kw $m] (:children $x)]
              (u/reg-compression! :compact-clj/or->get node $or (str "(" $kw " " $m " " $y ")")))

            (and (u/symbol? (first (:children $x)) "get")
                 (u/count? $x 3))
            (let [[_$get $m $k] (:children $x)]
              (u/reg-compression!
               :compact-clj/or->get
               node
               $or
               (str "(get " $m " " $k " " $y ")")))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt or-remove-nested or->some or->get) node)))
