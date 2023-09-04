(ns ^:no-doc hooks.not
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [node]
  (u/count? node 2))

(defn not->not=
  {:type :compact-clj/not->not=
   :example {:in '(not (= x y))
             :out '(not= x y)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$= & $=-args] (:children $x)]
    (when (and (u/list? $x)
               (u/symbol? $= "=")
               (seq $=-args))
      (u/reg-compression!
       :compact-clj/not->not=
       node
       $not
       (str "(not= " (str/join " " $=-args) ")")))))

(defn not->not-any?
  {:type :compact-clj/not->not-any?
   :example {:in '(not (some f coll))
             :out '(not-any? f coll)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$some $pred $coll] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 3)
               (u/symbol? $some "some"))
      (u/reg-compression!
       :compact-clj/not->not-any?
       node
       $not
       (str "(not-any? " $pred " " $coll ")")))))

(defn not->boolean
  {:type :compact-clj/not->boolean
   :example {:in '(not (not x))
             :out '(boolean x)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$x-not $x-x] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $x-not "not"))
      (u/reg-compression!
       :compact-clj/not->boolean
       node
       $not
       (str "(boolean " $x-x ")")))))

(defn not->not-every?
  {:type :compact-clj/not->not-every?
   :example {:in '(not (every? pred coll))
             :out '(not-every? pred coll)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$every? $pred $coll] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 3)
               (u/symbol? $every? "every?"))
      (u/reg-compression!
       :compact-clj/not->not-every?
       node
       $not
       (str "(not-every? " $pred " " $coll ")")))))

(defn not->seq
  {:type :compact-clj/not->seq
   :unsafe [:boolean->logical]
   :example {:in '(not (empty? coll))
             :out '(seq coll)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$empty? $coll] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $empty? "empty?"))
      (u/reg-compression! :compact-clj/not->seq node $not (str "(seq " $coll ")")))))

(defn not->even?
  {:type :compact-clj/not->even?
   :example {:in '(not (odd? n))
             :out '(even? n)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$odd? $n] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $odd? "odd?"))
      (u/reg-compression! :compact-clj/not->even? node $not (str "(even? " $n ")")))))

(defn not->odd?
  {:type :compact-clj/not->odd?
   :example {:in '(not (even? n))
             :out '(odd? n)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$even? $n] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $even? "even?"))
      (u/reg-compression! :compact-clj/not->odd? node $not (str "(odd? " $n ")")))))

(defn not->some?
  {:type :compact-clj/not->some?
   :example {:in '(not (nil? x))
             :out '(some? x)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$nil? $x-x] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $nil? "nil?"))
      (u/reg-compression! :compact-clj/not->some? node $not (str "(some? " $x-x ")")))))

(defn not->empty?
  {:type :compact-clj/not->empty?
   :example {:in '(not (seq coll))
             :out '(empty? coll)}}
  [{:keys [children] :as node}]
  (let [[$not $x] children
        [$seq $coll] (:children $x)]
    (when (and (u/list? $x)
               (u/count? $x 2)
               (u/symbol? $seq "seq"))
      (u/reg-compression! :compact-clj/not->empty? node $not (str "(empty? " $coll ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt not->not= not->not-any? not->boolean not->not-every?
           not->seq not->even? not->odd? not->some? not->empty?) node)))
