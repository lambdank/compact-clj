(ns ^:no-doc hooks.when-let
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn when-let->when-first [node]
  (let [{[$when-let $bindings & $body] :children} node
        {[$bindings-1 $bindings-2 & $bindings-rest] :children} $bindings
        {[$first $coll] :children} $bindings-2]
    (when (and (empty? $bindings-rest)
               (u/symbol? $first "first"))
      (api/reg-finding!
       (assoc (meta $when-let)
              :message (u/->msg node (str "(when-first [" $bindings-1 " " $coll "] " (str/join " " $body)")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (when-let->when-first node)))
