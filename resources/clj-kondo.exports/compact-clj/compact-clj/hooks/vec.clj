(ns ^:no-doc hooks.vec
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn vec->mapv [node]
  (let [children (:children node)
        [$vec $1] children
        [$map $f & $colls] (:children $1)]
    (when (and (= (count children) 2)
               (u/symbol? $map "map"))
      (api/reg-finding! (merge (meta $vec)
                               {:message (u/->msg node (str "(mapv " $f " " (str/join " " $colls) ")"))
                                :type :lol})))))

(defn vec->filterv [node]
  (let [children (:children node)
        [$vec $1] children
        [$filter $pred $coll] (:children $1)]
    (when (and (= (count children) 2)
               (u/symbol? $filter "filter"))
      (api/reg-finding! (merge (meta $vec)
                               {:message (u/->msg node (str "(filterv " $pred " " $coll ")"))
                                :type :lol})))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (vec->mapv node)))
