(ns ^:no-doc hooks.eq
  (:require
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn- legal? [node]
  (pos? (count (:children node))))

(defn =-remove-nested
  {:type :compact-clj/=-remove-nested
   :example {:in '(= (= x y) z)
             :out '(= x y z)}}
  [{[_$comp & $args] :children}]
  (->> $args
       (u/associative-lift #(u/symbol? % "="))
       (map (fn [[nested-node $nested-and $nested-args]]
              (u/reg-compression!
               :compact-clj/=-remove-nested
               nested-node
               $nested-and
               (str/join " " $nested-args))))
       seq))

(defn =->true?
  {:type :compact-clj/=->true?
   :example {:in '(= x true)
             :out '(true? x)}}
  [{:keys [children] :as node}]
  (let [[$= $x $y] children]
    (when (u/count? node 3)
      (cond (u/symbol? $x "true")
            (u/reg-compression! :compact-clj/=->true? node $= (str "(true? " $y ")"))

            (u/symbol? $y "true")
            (u/reg-compression! :compact-clj/=->true? node $= (str "(true? " $x ")"))))))

(defn =->nil?
  {:type :compact-clj/=->nil?
   :example {:in '(= x nil)
             :out '(nil? x)}}
  [{:keys [children] :as node}]
  (let [[$= $x $y] children]
    (when (u/count? node 3)
      (cond (u/symbol? $x "nil")
            (u/reg-compression! :compact-clj/=->nil? node $= (str "(nil? " $y ")"))

            (u/symbol? $y "nil")
            (u/reg-compression! :compact-clj/=->nil? node $= (str "(nil? " $x ")"))))))

(defn =->empty?
  {:type :compact-clj/=->empty?
   :example {:in '(= 0 (count coll))
             :out '(empty? coll)}}
  [{:keys [children] :as node}]
  (let [[$= $x $y] children
        [$x-count $x-coll] (:children $x)
        [$y-count $y-coll] (:children $y)]
    (when (u/count? node 3)
      (cond (and (u/symbol? $x "0")
                 (u/list? $y)
                 (u/symbol? $y-count "count"))
            (u/reg-compression! :compact-clj/=->empty? node $= (str "(empty? " $y-coll ")"))

            (and (u/symbol? $y "0")
                 (u/list? $x)
                 (u/symbol? $x-count "count"))
            (u/reg-compression! :compact-clj/=->empty? node $= (str "(empty? " $x-coll ")"))))))

(defn =-remove-duplicate
  {:type :compact-clj/=-remove-duplicate
   :example {:in '(= x x y)
             :out '(= x y)}}
  [{:keys [children] :as node}]
  (let [[$= & $args] children]
    (when (not-empty (->> $args
                          (map u/->sexpr)
                          frequencies
                          (map second)
                          (filter #(< 1 %))))
      (u/reg-compression!
       :compact-clj/=-remove-duplicate
       node
       $=
       (str "(= " (->> $args
                       (reduce (fn [[acc sexprs] x]
                                 (let [x-sexpr (u/->sexpr x)]
                                   (if (contains? sexprs x-sexpr)
                                     [acc sexprs]
                                     [(conj acc x) (conj sexprs x-sexpr)])))
                               [[] #{}])
                       first
                       (str/join " " ))
            ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt =->true? =->nil? =->empty? =-remove-duplicate =-remove-nested) node)))
