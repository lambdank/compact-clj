(ns ^:no-doc hooks.into
  (:require
   [clj-kondo.hooks-api :as api]
   [hooks.utils :as u]))

(defn into->vec [node]
  (let [{[$into $to $from] :children} node]
    (when (and (every-pred u/empty? u/vector?) $to)
      (api/reg-finding!
       (merge (meta $into)
              {:message (u/->msg node (str "(vec " $from ")"))
               :type :lol})))))

(defn into->set [node]
  (let [{[$into $to $from] :children} node]
    (when ((every-pred u/empty? u/set?) $to)
      (api/reg-finding!
       (merge (meta $into)
              {:message (u/->msg node (str "(set " $from ")"))
               :type :lol})))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt into->set into->vec) node)))
