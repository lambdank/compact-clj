(ns ^:no-doc hooks.max
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (<= 2 (count children)))

(defn max-remove-nested
  {:type :compact-clj/max-remove-nested
   :example {:in '(max (max x y) z)
             :out '(max x y z)}}
  [{[_$and & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "max"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/max-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (max-remove-nested node)))
