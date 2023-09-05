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
             :out '(some f [x y])}
   :unsafe [:false->nil]}
  [{:keys [children] :as node}]
  (let [[$or & $args] children
        {[$pred] :children} (first $args)]
    (when (and (< 1 (count $args))
               (every? #(and (u/list? %) (u/count? % 2)) $args)
               (every? (fn [{[pred] :children}] (= pred $pred)) (rest $args)))
      (u/reg-compression!
       :compact-clj/or->some
       node
       $or
       (str "(some " $pred " [" (str/join " " (map (comp second :children) $args)) "])")))))

(defn or->contains?
  {:type :compact-clj/or->contains?
   :example {:in '(or (= x y) (= x z))
             :out '(contains? #{y z} x)}}
  [{:keys [children] :as node}]
  (let [[$or & $args] children
        [_ $y $z] (:children (first $args))
        =-pairs (map #(rest (:children %)) $args)]
    (when (and
           (< 1 (count $args))
           (every? (fn [{[$=] :children :as $arg}]
                     (and (u/list? $arg)
                          (u/count? $arg 3)
                          (u/symbol? $= "="))) (rest $args)))
      (cond (every? #(some (partial u/code= $y) %) =-pairs)
            (u/reg-compression!
             :compact-clj/or->contains?
             node
             $or
             (str "(contains? "
                  (->> =-pairs
                       (map (fn [[x y]] (if (= x $y) y x)))
                       distinct
                       u/->set)
                  " " $y ")"))

            (every? #(some (partial u/code= $z) %) =-pairs)
            (u/reg-compression!
             :compact-clj/or->contains?
             node
             $or
             (str "(contains? "
                  (->> =-pairs
                       (map (fn [[x y]] (if (= x $z) y x)))
                       distinct
                       u/->set)
                  " " $z ")"))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt or-remove-nested or->some or->contains?) node)))
