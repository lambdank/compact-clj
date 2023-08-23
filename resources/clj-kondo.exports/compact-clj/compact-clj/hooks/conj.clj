(ns ^:no-doc hooks.conj
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (<= 3 (count children)))

(defn conj-remove-nested [{[$conj $coll & $xs] :children :as node}]
  (let [{[$nested-conj $nested-coll & $nested-xs] :children} $coll]
    (when (and (u/symbol? $nested-conj "conj")
               (seq $nested-xs))
      (u/reg-compression!
       node
       $conj
       (str "(conj " $nested-coll " " (str/join " " (concat $nested-xs $xs)) ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (conj-remove-nested node)))
