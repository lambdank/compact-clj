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

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (vec->mapv node)))
