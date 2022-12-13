(ns ^:no-doc hooks.filter
  (:require
   [clj-kondo.hooks-api :as api]
   [hooks.utils :as u]))

(defn filter->remove [{:keys [children] :as node}]
  (let [[$filter $pred $coll] children
        {[$pred-1 $pred-2 $pred-3] :children} $pred
        {[$pred-3-1 $pred-3-2] :children} $pred-3]
    (cond
      (u/symbol? $pred-1 "complement")
      (api/reg-finding!
       (assoc (meta $filter)
              :message (u/->msg node (str "(remove " $pred-2 " " $coll ")"))
              :type :lol))

      (and (u/symbol? $pred-1 "fn") (u/symbol? $pred-3-1 "not"))
      (api/reg-finding!
       (assoc (meta $filter)
              :message (u/->msg node (str "(remove (fn " $pred-2 " " $pred-3-2 ") " $coll ")"))
              :type :lol)))))

(defn filter->keep [{:keys [children] :as node}]
  (let [[$filter $pred $coll] children
        {[$coll-1 $coll-2 $coll-3 & $coll-args] :children} $coll]
    (when (and (u/symbol? $pred "identity")
               (u/symbol? $coll-1 "map")
               (empty? $coll-args))
      (api/reg-finding!
       (assoc (meta $filter)
              :message (u/->msg node (str "(keep " $coll-2 " " $coll-3 ")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt filter->remove filter->keep) node)))
