(ns ^:no-doc hooks.filter
  (:require
   [hooks.utils :as u]))

(defn- legal? [node]
  (u/count? node 3))

(defn filter->remove
  {:example {:in '(filter (fn [x] (not (f x))) coll)
             :out '(remove (fn [x] (f x)) coll)}}
  [{:keys [children] :as node}]
  (let [[$filter $pred $coll] children
        {[$pred-1 $pred-2 $pred-3] :children} $pred]
    (cond
      ;; (filter (complent pred) coll) -> (remove pred coll)
      (and (u/list? $pred)
           (u/symbol? $pred-1 "complement")
           (u/count? $pred 2))
      (u/reg-compression! node $filter (str "(remove " $pred-2 " " $coll ")"))

      ;; (filter #(not (pred %)) coll) -> (remove #(pred %) coll)
      (and (u/fn? $pred)
           (u/count? $pred 2)
           (u/symbol? $pred-1 "not")
           (u/list? $pred-2)
           (not (u/empty? $pred-2)))
      (u/reg-compression!
       node
       $filter
       (str "(remove #" $pred-2 " " $coll ")"))

      ;; (filter (fn [x] (not (pred x))) coll) -> (remove (fn [x] (pred x))) coll)
      (and (u/list? $pred)
           (u/symbol? $pred-1 "fn")
           (u/count? $pred 3)
           (u/list? $pred-3)
           (u/symbol? (first (:children $pred-3)) "not")
           (u/count? $pred-3 2))
      (u/reg-compression!
       node
       $filter
       (str "(remove (fn " $pred-2 " " (second (:children $pred-3)) ") " $coll ")")))))

(defn filter->keep
  {:example {:in '(filter some? (map f coll))
             :out '(keep f coll)}}
  [{:keys [children] :as node}]
  (let [[$filter $pred $coll] children
        {[$map $f & $colls] :children} $coll]
    (when (and (u/symbol? $pred "some?")
               (u/symbol? $map "map")
               (= 1 (count $colls)))
      (u/reg-compression!
       node
       $filter
       (str "(keep " $f " " (first $colls) ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt filter->remove filter->keep) node)))
