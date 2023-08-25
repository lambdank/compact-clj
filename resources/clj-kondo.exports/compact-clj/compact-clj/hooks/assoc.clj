(ns ^:no-doc hooks.assoc
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ((every-pred even? #(<= 4 %)) (count children)))

(defn assoc-remove-nested
  "Compression: (assoc (assoc m :a x) :b y) -> (assoc m :a x :b y)"
  [{[$assoc $m & $kvs] :children :as node}]
  (let [{[$nested-assoc $nested-m & $nested-kvs] :children} $m]
    (when (and (u/list? $m)
               (u/symbol? $nested-assoc "assoc")
               (even? (count $nested-kvs)))
      (u/reg-compression!
       node
       $assoc
       (str "(assoc " $nested-m " " (str/join " " (concat $nested-kvs $kvs)) ")")))))

(defn assoc->assoc-in
  "Compression: (assoc m :a (assoc (:a m) :b x)) -> (assoc-in m [:a :b] x)"
  [{[$assoc $m $k $v] :children :as node}]
  (let [{[$v-assoc $v-m $v-k $v-v] :children} $v
        {[$v-m-1 $v-m-2 $v-m-3] :children} $v-m
        complex-pattern (or
                         ;; (assoc coll key (assoc (key coll) key1 val))
                         (and (u/count? $v-m 2)
                              (u/code= $k $v-m-1)
                              (u/code= $m $v-m-2))
                         ;; (assoc coll key (assoc (coll key) key1 val))
                         (and (u/count? $v-m 2)
                              (u/code= $k $v-m-2)
                              (u/code= $m $v-m-1))
                         ;; (assoc coll key (assoc (get coll key) key1 val))
                         (and (u/symbol? "get" $v-m-1)
                              (u/count? $v-m 3)
                              (u/code= $m $v-m-2)
                              (u/code= $k $v-m-3)))]
    (when (and (u/list? $v)
               (u/symbol? $v-assoc "assoc")
               (u/list? $v-m)
               complex-pattern)
      (u/reg-compression! node $assoc (str "(assoc-in " $m " [" $k " " $v-k "] " $v-v ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt assoc->assoc-in assoc-remove-nested) node)))

