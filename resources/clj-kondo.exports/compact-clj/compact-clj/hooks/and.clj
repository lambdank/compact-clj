(ns ^:no-doc hooks.and
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ;; `and` with one argument is already linted by clj-kondo.
  (<= 2 (count children)))

(defn and-remove-nested
  "Compression: (and (and x y) z) -> (and x y z)"
  [{[_$and & $args] :children}]
  (->> $args
       (keep (fn [{[$nested-and & $nested-args] :children :as nested-and-node}]
               (when (and (u/list? nested-and-node)
                          (u/symbol? $nested-and "and")
                          (<= 2 (count (:children nested-and-node))))
                 (u/reg-compression! nested-and-node $nested-and (str/join " " $nested-args)))))
       doall))

(defn and->every?
  "Compression: (and (f x) (f y)) -> (every? f [x y])"
  [{[$and {[$pred $x] :children} & $args] :children :as node}]
  (when (and (every? #(u/count? % 2) $args)
             (every? #{$pred} (map (comp first :children) $args)))
    (u/reg-compression!
     node
     $and
     (str "(every? " $pred " [" $x " " (str/join " " (map (comp second :children) $args)) "])"))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt and-remove-nested and->every?) node)))
