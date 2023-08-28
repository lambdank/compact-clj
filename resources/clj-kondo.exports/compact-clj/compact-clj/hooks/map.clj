(ns ^:no-doc hooks.map
  (:require
   [hooks.utils :as u]))

(defn- legal? [node]
  (u/count? node 3))

(defn map-remove-nested
  {:example {:in '(map f (map g coll))
             :out '(map (comp f g) coll)}}
  [{:keys [children] :as node}]
  (let [[$map $f $coll] children
        [$nested-map $nested-f $nested-coll] (:children $coll)]
    (when (and (u/symbol? $nested-map "map")
               (u/count? $coll 3))
      (u/reg-compression! node $map (str "(map (comp " $f " " $nested-f ") " $nested-coll ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (map-remove-nested node)))
