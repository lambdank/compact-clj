(ns ^:no-doc hooks.utils
  (:require
   [clj-kondo.hooks-api :as api]))


(defn ->msg [old new]
  (str old " -shorten-> " new))

(defn list? [{:keys [tag]}]
  (= tag :list))

(defn vector? [{:keys [tag]}]
  (= tag :vector))

(defn reg-compression! [root-node highlight-node compression]
  (api/reg-finding!
   (assoc (meta highlight-node)
          :message (->msg root-node compression)
          :type :lol)))

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

(defn in-source?
  "Detect whether the hook is in the source.
  Useful for removing linting errors in macro-expansions."
  [node]
  (= 4 (count (select-keys (meta (first (:children node))) [:col :end-col :row :end-row]))))
