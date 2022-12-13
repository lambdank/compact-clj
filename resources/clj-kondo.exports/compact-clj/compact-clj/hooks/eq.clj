(ns ^:no-doc hooks.eq
  (:require
   [clj-kondo.hooks-api :as api]
   [hooks.utils :as u]))

(defn =->true? [node]
  (let [children (:children node)
        [$= $1 $2] children
        finding #(merge (meta $=)
                        {:message (u/->msg node (str "(true? " % ")"))
                         :type :lol})]
    (when (= (count children) 3)
      (cond (u/symbol? $1 "true") (api/reg-finding! (finding $2))
            (u/symbol? $2 "true") (api/reg-finding! (finding $1))))))

(defn =->nil? [node]
  (let [children (:children node)
        [$= $1 $2] children
        finding #(merge (meta $=)
                        {:message (u/->msg node (str "(nil? " % ")"))
                         :type :lol})]
    (when (= (count children) 3)
      (cond (u/symbol? $1 "nil") (api/reg-finding! (finding $2))
            (u/symbol? $2 "nil") (api/reg-finding! (finding $1))))))

(defn =->empty? [node]
  (let [children (:children node)
        [$= $1 $2] children
        {[$1-1 $1-2] :children} $1
        {[$2-1 $2-2] :children} $2
        finding #(merge (meta $=)
                        {:message (u/->msg node (str "(empty? " % ")"))
                         :type :lol})]
    (when (= (count children) 3)
      (cond (and (u/symbol? $1 "0") (u/symbol? $2-1 "count")) (api/reg-finding! (finding $2-2))
            (and (u/symbol? $1-1 "count") (u/symbol? $2 "0")) (api/reg-finding! (finding $1-2))))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt =->true? =->nil? =->empty?) node)))
