(ns ^:no-doc hooks.if
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn if->if-not [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        [$test-1 $test-2] (:children $test)]
    (when (and (u/list? $test)
               (= 2 (count (:children $test)))
               (u/symbol? $test-1 "not"))
      (api/reg-finding!
       (assoc (meta $if)
              :message (u/->msg node (str "(if-not " $test-2 " " $then " " $else ")"))
              :type :lol)))))

(defn if->when [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (u/symbol? $else "nil")
      (api/reg-finding!
       (assoc (meta $if)
              :message (u/->msg node (str "(when " $test " " $then ")"))
              :type :lol)))))

(defn if->boolean [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (and (u/symbol? $then "true")
               (u/symbol? $else "false"))
      (api/reg-finding!
       (assoc (meta $if)
              :message (u/->msg node (str "(boolean " $test ")"))
              :type :lol)))))

(defn if->not [{:keys [children] :as node}]
  (let [[$if $test $then $else] children]
    (when (and (u/symbol? $then "false")
               (u/symbol? $else "true"))
      (api/reg-finding!
       (assoc (meta $if)
              :message (u/->msg node (str "(not " $test ")"))
              :type :lol)))))

(defn if->cond-> [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        [$then-1 $then-2 & $then-args] (:children $then)]
    (when (and (= 4 (count children))
               (u/list? $then)
               (<= 2 (count (:children $then)))
               (= (u/->sexpr $else) (u/->sexpr $then-2)))
      (api/reg-finding!
       (assoc (meta $if)
              :message (u/->msg node (str "(cond-> " $else " "
                                          $test " ("  (str/join " " (conj $then-args $then-1)) ")" ")"))
              :type :lol)))))

(defn if-move-to-inner [{:keys [children] :as node}]
  (let [[$if $test $then $else] children
        {[& $then-args] :children} $then
        {[& $else-args] :children} $else]
    (when (and (= (count $then-args) (count $else-args))
               (u/list? $then-args)
               (u/list? $else-args))
      (let [pairs (partition 2 (interleave $then-args $else-args))
            diff (keep-indexed #(when-not (= (first %2) (second %2)) [%1 %2]) pairs)]
        (when (= (count diff) 1)
          (let [[[i [t e]]] diff]
            (api/reg-finding!
             (assoc (meta $if)
                    :message (u/->msg node
                                      (str "(" (str/join " " (take i $then-args))
                                           " (if " $test " " t " " e ") "
                                           (str/join " " (drop (inc i) $then-args)) ")"))
                    :type :lol))))))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt if->if-not if->when if->boolean if->not if->cond-> if-move-to-inner) node)))
