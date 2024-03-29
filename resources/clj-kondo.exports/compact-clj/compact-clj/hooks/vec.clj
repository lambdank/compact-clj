(ns ^:no-doc hooks.vec
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [node]
  (u/count? node 2))

(defn vec->mapv
  {:type :compact-clj/vec->mapv
   :example {:in '(vec (map f coll))
             :out '(mapv f coll)}}
  [{:keys [children] :as node}]
  (let [[$vec $coll] children
        [$map $f & $colls] (:children $coll)]
    (when (and (seq $colls)
               (u/symbol? $map "map"))
      (u/reg-compression!
       :compact-clj/vec->mapv
       node
       $vec
       (str "(mapv " $f " " (str/join " " $colls) ")")))))

(defn vec->filterv
  {:type :compact-clj/vec->filterv
   :example {:in '(vec (filter pred coll))
             :out '(filterv pred coll)}}
  [{:keys [children] :as node}]
  (let [[$vec $coll] children
        [$filter $pred $nested-coll] (:children $coll)]
    (when (and (u/count? $coll 3)
               (u/symbol? $filter "filter"))
      (u/reg-compression!
       :compact-clj/vec->filterv
       node
       $vec
       (str "(filterv " $pred " " $nested-coll ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt vec->mapv vec->filterv) node)))
