(ns ^:no-doc hooks.first
  (:require
   [clj-kondo.hooks-api :as api]
   [hooks.utils :as u]))

(defn first->ffirst [node]
  (let [children (:children node)
        [$first $1] children
        [$first-first $coll] (:children $1)]
    (when (and (= (count children) 2)
               (u/symbol? $first-first "first"))
      (api/reg-finding! (merge (meta $first)
                               {:message (u/->msg node (str "(ffirst " $coll  ")"))
                                :type :lol})))))

(defn first->second [node]
  (let [children (:children node)
        [$first $1] children
        [$next $coll] (:children $1)]
    (when (and (= (count children) 2)
               (u/symbol? $next "next"))
      (api/reg-finding! (merge (meta $first)
                               {:message (u/->msg node (str "(second " $coll  ")"))
                                :type :lol})))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt first->ffirst first->second) node)))

