(ns ^:no-doc hooks.conj
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (<= 3 (count children)))

(defn conj-remove-nested
  {:type :compact-clj/conj-remove-nested
   :example {:in '(conj (conj coll x) y)
             :out '(conj coll x y)}}
  [{[_$conj $coll] :children}]
  (let [{[$nested-conj $nested-coll & $nested-xs] :children} $coll]
    (when (and (u/symbol? $nested-conj "conj")
               (seq $nested-xs))
      (u/reg-compression!
       :compact-clj/conj-remove-nested
       $coll
       $nested-conj
       (str/join " " (conj $nested-xs $nested-coll))))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (conj-remove-nested node)))
