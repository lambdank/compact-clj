(ns ^:no-doc hooks.utils
  (:require
   [clj-kondo.hooks-api :as api]))

(defn ->msg [old new]
  (str old " -shorten-> " new))

(defn fn? [n]
  ;; This throws a "Could not resolve symbol: clj_kondo.impl.rewrite_clj.node.fn.FnNode"
  #_(instance? clj_kondo.impl.rewrite_clj.node.fn.FnNode n)
  (= "class clj_kondo.impl.rewrite_clj.node.fn.FnNode" (str (class n))))

(defn map? [node]
  (api/map-node? node))

(defn list? [{:keys [tag]}]
  (= tag :list))

(defn vector? [{:keys [tag]}]
  (= tag :vector))

(defn reg-compression! [tipe root-node highlight-node compression]
  (api/reg-finding!
   (assoc (meta highlight-node)
          :message (->msg root-node compression)
          :type tipe)))

(defn set? [{:keys [tag]}]
  (= tag :set))

(defn count? [{:keys [children]} size]
  (= size (count children)))

(defn symbol? [{:keys [string-value]} s]
  (= string-value s))

(defn keyword? [node]
  (api/keyword-node? node))

(defn coll? [{:keys [tag]}]
  (#{:list :vector :set} tag))

(defn empty? [{:keys [children]}]
  (zero? (count children)))

(defn ->sexpr [node]
  (some-> node api/sexpr))

(defn code= [& args]
  (->> args
       (map ->sexpr)
       (apply =)))

(defn in-source?
  "Detect whether the hook is in the source.
  Useful for removing linting errors in macro-expansions."
  [node]
  (= 4 (count (select-keys (meta (first (:children node))) [:col :end-col :row :end-row]))))
