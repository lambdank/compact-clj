(ns ^:no-doc hooks.and
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [hooks.utils :as u]))

(defn and-remove-nested [{[_$and & $args] :children}]
  (doall
   (keep (fn [{[$1 & $1-args] :children :as $1-node}]
           (when (u/symbol? $1 "and")
             (api/reg-finding!
              (merge (meta $1)
                     {:message (u/->msg $1-node (str/join " " $1-args))
                      :type :lol}))))
         $args)))

(defn and->every? [node]
  (let [{[$and & $args] :children} node]
    (when (and (pos? (count $args))
               (every? (fn [{:keys [children]}] (= (count children) 2)) $args)
               (apply = (map (comp u/->sexpr first :children) $args)))
      (let [pred (-> $args first :children first)
            args (map (comp second :children) $args)]
        (api/reg-finding!
         (merge (meta $and)
                {:message (u/->msg node (str "(every? " pred " [" (str/join " " args) "])"))
                 :type :lol}))))))

(defn all [{:keys [node]}]
  (when (u/in-source? node)
    ((juxt and-remove-nested and->every?) node)))
