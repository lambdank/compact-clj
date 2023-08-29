(ns ^:no-doc hooks.comp
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (<= 2 (count children)))

(defn comp-remove-nested
  {:type :compact-clj/comp-remove-nested
   :example {:in '(comp (comp f g) h)
             :out '(comp f g h)}}
  [{[_$comp & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "comp"))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/comp-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (comp-remove-nested node)))
