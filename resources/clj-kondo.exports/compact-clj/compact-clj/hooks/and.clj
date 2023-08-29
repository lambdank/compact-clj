(ns ^:no-doc hooks.and
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ;; `and` with one argument is already linted by clj-kondo.
  (<= 2 (count children)))

(defn and-remove-nested
  {:type :compact-clj/and-remove-nested
   :example {:in '(and (and x y) z)
             :out '(and x y z)}}
  [{[_$and & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "and"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/and-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn and->every?
  {:type :compact-clj/and->every?
   :unsafe [:logical->boolean]
   :example {:in '(and (f x) (f y))
             :out '(every? f [x y])}}
  [{[$and {[$pred $x] :children} & $args] :children :as node}]
  (when (and (every? #(u/count? % 2) $args)
             (every? #{$pred} (map (comp first :children) $args)))
    (u/reg-compression!
     :compact-clj/and->every?
     node
     $and
     (str "(every? " $pred " [" $x " " (str/join " " (map (comp second :children) $args)) "])"))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt and-remove-nested and->every?) node)))
