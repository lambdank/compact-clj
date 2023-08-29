(ns ^:no-doc hooks.into
  (:require
   [hooks.utils :as u]))

(defn- legal? [node]
  ;; Only compressions for (into to from) currently
  (u/count? node 3))

(defn into->vec
  {:type :compact-clj/into->vec
   :example {:in '(into [] '(x y z))
             :out '(vec '(x y z))}}
  [{:keys [children] :as node}]
  (let [[$into $to $from] children]
    (when ((every-pred u/empty? u/vector?) $to)
      (u/reg-compression! :compact-clj/into->vec node $into (str "(vec " $from ")")))))

(defn into->set
  {:type :compact-clj/into->set
   :example {:in '(into #{} [x y z])
             :out '(set [x y z])}}
  [{:keys [children] :as node}]
  (let [[$into $to $from] children]
    (when ((every-pred u/empty? u/set?) $to)
      (u/reg-compression! :compact-clj/into->set node $into (str "(set " $from ")")))))

(defn all [{:keys [node]}]
  (when (and (u/in-source? node) (legal? node))
    ((juxt into->set into->vec) node)))
