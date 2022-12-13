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
  (let [{[$let $bindings $exprs] :children} node
        {[$bindings-1 $bindings-2 & $rest] :children} $bindings
        {[$when $test $body] :children} $exprs]
    (when (and (u/symbol? $when "when")
               (= (u/->sexpr $bindings-1) (u/->sexpr $test))
               (empty? $rest))
      (api/reg-finding!
       (assoc (meta $let)
              :message (u/->msg node (str "(when-let [" $bindings-1 " " $bindings-2 "] " $body ")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt let->doto let->when-let) node)))
