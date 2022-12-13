(ns ^:no-doc hooks.not
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn not->not= [{:keys [children] :as node}]
  (let [[$not $1] children
        [$1-1 & $1-args] (:children $1)]
    (when (and (u/list? $1) (u/symbol? $1-1 "="))
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(not= " (str/join " " $1-args) ")"))
              :type :lol)))))

(defn not->not-any? [{:keys [children] :as node}]
  (let [[$not $1] children
        [$1-1 $1-2 & $1-args] (:children $1)]
    (when (and (u/list? $1) (u/symbol? $1-1 "some"))
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg
                        node
                        (str "(not-any? " $1-2 " " (str/join " " $1-args) ")"))
              :type :lol)))))

(defn not->identity [{:keys [children] :as node}]
  (let [[$not {[$1-1 $1-2] :children}] children]
    (when (u/symbol? $1-1 "not")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node $1-2)
              :type :lol)))))

(defn not->not-every? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $pred $coll] :children}] children]
    (when (u/symbol? $1-1 "every?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(not-every? " $pred " " $coll ")"))
              :type :lol)))))

(defn not->seq [{:keys [children] :as node}]
  (let [[$not {[$1-1 $coll] :children}] children]
    (when (u/symbol? $1-1 "empty?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(seq " $coll ")"))
              :type :lol)))))

(defn not->even? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $n] :children}] children]
    (when (u/symbol? $1-1 "odd?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(even? " $n ")"))
              :type :lol)))))

(defn not->odd? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $n] :children}] children]
    (when (u/symbol? $1-1 "even?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(odd? " $n ")"))
              :type :lol)))))

(defn not->true? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $x] :children}] children]
    (when (u/symbol? $1-1 "false?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(true? " $x ")"))
              :type :lol)))))

(defn not->false? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $x] :children}] children]
    (when (u/symbol? $1-1 "true?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(false? " $x ")"))
              :type :lol)))))

(defn not->some? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $1-2] :children}] children]
    (when (u/symbol? $1-1 "nil?")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(some? " $1-2 ")"))
              :type :lol)))))

(defn not->empty? [{:keys [children] :as node}]
  (let [[$not {[$1-1 $1-2] :children}] children]
    (when (u/symbol? $1-1 "seq")
      (api/reg-finding!
       (assoc (meta $not)
              :message (u/->msg node (str "(empty? " $1-2 ")"))
              :type :lol)))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt not->not= not->not-any? not->identity not->not-every?
           not->seq not->even? not->odd? not->true? not->false?
           not->some? not->empty?) node)))
