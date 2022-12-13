(ns ^:no-doc hooks.map
  (:require
   [clj-kondo.hooks-api :as api]
   [hooks.utils :as u]))

(defn map-remove-nested [{:keys [children] :as node}]
  (let [[$map $f $coll] children
        $coll-children (:children $coll)
        [$coll-1 $coll-2 $coll-3] $coll-children]
    (when (and (= (count children) (count $coll-children) 3)
               (u/symbol? $coll-1 "map"))
      (api/reg-finding!
       (merge (meta $map)
              {:message (u/->msg node (str "(map (comp " $f " " $coll-2 ") " $coll-3 ")"))
               :type :lol})))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (map-remove-nested node)))
