(ns ^:no-doc hooks.utils
  (:require
   [clj-kondo.hooks-api :as api]))

(defn ->msg [old new]
  (str old " -shorten-> " new))

(defn list? [{:keys [tag]}]
  (= tag :list))

(defn vector? [{:keys [tag]}]
  (= tag :vector))

(defn set? [{:keys [tag]}]
  (= tag :set))

(defn symbol? [{:keys [string-value]} s]
  (= string-value s))

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
