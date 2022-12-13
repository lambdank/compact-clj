(ns ^:no-doc hooks.assoc
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn assoc-remove-nested [node]
  (let [{[$assoc $map $key $val] :children} node
        {[$map-1 $map-2 & $map-args] :children} $map]
    (when (u/symbol? $map-1 "assoc")
      (api/reg-finding!
       (assoc (meta $assoc)
              :message (u/->msg node (str "(assoc " $map-2 " " (str/join " " $map-args)
                                          " " $key " " $val ")"))
              :type :lol)))))

(defn assoc->assoc-in [node]
  (let [{[$assoc $map $key $val] :children} node
        {[$1 $2 $3 $4] :children} $val
        {[$2-1 $2-2 $2-3] :children} $2
        result (str "(assoc-in " $map " [" $key " " $3 "] " $4 ")")
        finding (assoc (meta $assoc) :message (u/->msg node result) :type :lol)
        complex-pattern (or
                         ;; (assoc coll key (assoc (key coll) key1 val))
                         (and (= (u/->sexpr $key) (u/->sexpr $2-1))
                              (= (u/->sexpr $map) (u/->sexpr $2-2)))
                         ;; (assoc coll key (assoc (coll key) key1 val))
                         (and (= (u/->sexpr $key) (u/->sexpr $2-2))
                              (= (u/->sexpr $map) (u/->sexpr $2-1)))
                         ;; (assoc coll key (assoc (get coll key) key1 val))
                         (and (u/symbol? "get" $2-1)
                              (= (u/->sexpr $map) (u/->sexpr $2-2))
                              (= (u/->sexpr $key) (u/->sexpr $2-3))))]
    (when (and (u/list? $val)
               (u/symbol? $1 "assoc")
               (u/list? $2)
               complex-pattern)
      (api/reg-finding! finding))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt assoc->assoc-in assoc-remove-nested) node)))
