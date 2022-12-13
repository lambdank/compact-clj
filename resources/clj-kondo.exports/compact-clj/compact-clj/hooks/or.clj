(ns ^:no-doc hooks.or
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn or->some [node]
  (let [{[$or & $args] :children} node]
    (when (and (pos? (count $args))
               (every? (fn [{:keys [children]}] (= (count children) 2)) $args)
               (apply = (map (comp u/->sexpr first :children) $args)))
      (let [pred (-> $args first :children first)
            args (map (comp second :children) $args)]
        (api/reg-finding!
         (merge (meta $or)
                {:message (u/->msg node (str "(some " pred " [" (str/join " " args) "])"))
                 :type :lol}))))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    (or->some node)))
