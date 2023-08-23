(ns ^:no-doc hooks.let
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn let->doto [node]
  (let [{[$let $bindings & $exprs] :children} node
        {[$var $value] :children} $bindings
        $vexprs (into [] $exprs)
        $var-sexpr (u/->sexpr $var)]
    (when (and (-> $bindings :children count (= 2))
               (= (u/->sexpr (peek $vexprs))
                  (u/->sexpr (first (:children $bindings))))
               (every? (fn [{:keys [children]}]
                         (= (u/->sexpr (second children)) $var-sexpr))
                       (drop-last $vexprs)))
      (println (drop-last $vexprs))
      (let [remove-var (fn [children] (keep-indexed #(when-not (= 1 %1) %2) children))
            exprs-without-var (->> $vexprs
                                   drop-last
                                   (map (fn [expr] (update expr :children remove-var))))]
        (api/reg-finding!
         (assoc (meta $let)
                :message (u/->msg node (str "(doto " $value " "
                                            (str/join " " exprs-without-var) ")"))
                :type :lol))))))

(defn let->when-let [node]
  (let [[$let $bindings $body] (:children node)
        [$bindings-1] (:children $bindings)
        [$body-1 $body-2 & $body-args] (:children $body)]
    (when (and (= 3 (count (:children node)))
               (u/vector? $bindings)
               (= 2 (count (:children $bindings)))
               (= $bindings-1 $body-2)
               (u/list? $body)
               (u/symbol? $body-1 "when"))
      (api/reg-finding!
       (assoc (meta $let)
              :message (u/->msg node (str "(when-let " $bindings " " (str/join " " $body-args) ")"))
              :type :lol)))))

(defn let->if-let [node]
  (let [[$let $bindings $body] (:children node)
        [$bindings-1] (:children $bindings)
        [$if $predicate $then $else] (:children $body)]
    (when (and (= 3 (count (:children node)))
               (u/vector? $bindings)
               (u/list? $body)
               (= 2 (count (:children $bindings)))
               (= 4 (count (:children $body)))
               (= $bindings-1 $predicate)
               (u/symbol? $if "if")
               (not-any? #{$predicate} (:children $else)))
      (api/reg-finding!
       (assoc (meta $let)
              :message (u/->msg node (str "(if-let " $bindings " " $then " " $else ")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt let->doto let->when-let) node)))
