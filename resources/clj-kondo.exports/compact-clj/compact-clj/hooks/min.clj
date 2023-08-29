(ns ^:no-doc hooks.min
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (<= 2 (count children)))

(defn min-remove-nested
  {:type :compact-clj/min-remove-nested
   :example {:in '(min (min x y) z)
             :out '(min x y z)}}
  [{[_$and & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "min"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/min-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (min-remove-nested node)))
