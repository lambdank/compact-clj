(ns ^:no-doc hooks.assoc
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  ((every-pred even? #(<= 4 %)) (count children)))

(defn assoc-remove-nested
  {:type :compact-clj/assoc-remove-nested
   :example {:in '(assoc (assoc m :a x) :b y)
             :out '(assoc m :a x :b y)}}
  [{[_$assoc $m] :children}]
  (let [{[$nested-assoc $nested-m & $nested-kvs] :children} $m]
    (when (and (u/list? $m)
               (u/symbol? $nested-assoc "assoc")
               (even? (count $nested-kvs)))
      (u/reg-compression!
       :compact-clj/assoc-remove-nested
       $m
       $nested-assoc
       (str $nested-m " " (str/join " " $nested-kvs))))))

(defn assoc->assoc-in
  {:type :compact-clj/assoc->assoc-in
   :example {:in '(assoc m :a (assoc (:a m) :b x))
             :out '(assoc-in m [:a :b] x)}}
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
                         (and (u/symbol? $v-m-1 "get")
                              (u/count? $v-m 3)
                              (u/code= $m $v-m-2)
                              (u/code= $k $v-m-3)))]
    (when (and (u/list? $v)
               (u/symbol? $v-assoc "assoc")
               (u/list? $v-m)
               complex-pattern)
      (u/reg-compression!
       :compact-clj/assoc->assoc-in
       node
       $assoc
       (str "(assoc-in " $m " [" $k " " $v-k "] " $v-v ")")))))

(defn assoc->update
  {:type :compact-clj/assoc->update
   :example {:in '(assoc m k (f (k m)))
             :out '(update m k f)}}
  [{[$assoc $m $k $v] :children :as node}]
  (let [[$f $x & $args] (:children $v)]
    (when (and (u/list? $v)
               (u/count? node 4)
               (u/list? $x)
               (or (let [[$get $nested-m $nested-k] (:children $x)]
                     (and (u/count? $x 3)
                          (u/symbol? $get "get")
                          (u/code= $nested-m $m)
                          (u/code= $nested-k $k)))
                   (let [[$nested-k $nested-m] (:children $x)]
                     (and (u/count? $x 2)
                          (u/code= $nested-m $m)
                          (u/code= $nested-k $k)))
                   (let [[$nested-m $nested-k] (:children $x)]
                     (and (u/count? $x 2)
                          (u/code= $nested-m $m)
                          (u/code= $nested-k $k)))))
      (u/reg-compression!
       :compact-clj/assoc->update
       node
       $assoc
       (str "(update " $m " " $k " " (cond-> $f (seq $args) (str " " (str/join " " $args))) ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt assoc->assoc-in assoc-remove-nested assoc->update) node)))
