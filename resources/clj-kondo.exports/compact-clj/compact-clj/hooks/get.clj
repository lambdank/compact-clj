(ns hooks.get
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [{:keys [children]}]
  (< 2 (count children)))

(defn get->kw
  {:type :compact-clj/get->kw
   :example {:in '(get m :a x)
             :out '(:a m x)}}
  [{:keys [children] :as node}]
  (let [[$get $map $key $not-found] children]
    (when (u/keyword? $key)
      (u/reg-compression!
       :compact-clj/get->kw
       node
       $get
       (str "(" $key " " $map (when (some? $not-found) (str " " $not-found)) ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    (get->kw node)))
