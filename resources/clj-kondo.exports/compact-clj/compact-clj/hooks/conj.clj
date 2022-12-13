(ns ^:no-doc hooks.conj
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn conj-remove-nested [node]
  (let [{[$conj $coll & $xs] :children} node
        {[$coll-conj $coll-coll & $coll-xs] :children} $coll]
    (when (and (u/symbol? $coll-conj "conj")
               (every? (comp pos? count) [$xs $coll-xs]))
      (api/reg-finding!
       (assoc (meta $conj)
              :message (u/->msg node (str "(conj " $coll-coll " " (str/join " " (concat $coll-xs $xs)) ")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (conj-remove-nested node)))
