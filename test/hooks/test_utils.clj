(ns hooks.test-utils
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [is]]
   [hooks.utils :as u]
   [malli.generator :as mg]))

(defn test-example!
  "Tests the rule using `in` and `out`.
  Uses metadata example of rule if `in` and `out` are not provided provided."
  [var-object & {:keys [in out]
                 :or {in (-> var-object meta :example :in)
                      out (-> var-object meta :example :out)}}]
  (let [{:keys [type compression root-node]} ((deref var-object) (api/parse-string (str in)))]
    (is (= type (:type (meta var-object))))
    (is (= (str in) (str root-node)))
    (is (= (str out) compression))))

(defn equivalent? [rule input & {:keys [f-out f-in]
                                 :or {f-out identity
                                      f-in identity}}]
  (let [output (-> input api/parse-string rule)]
    (when (contains? output :compression)
      (= (f-in (load-string input))
         (f-out (load-string (:compression output)))))))

(defn mock-reg-compression [f]
  (with-redefs [u/reg-compression! (fn [tipe root-node highlight-node compression]
                                     {:type tipe
                                      :root-node (str root-node)
                                      :highlight-node (str highlight-node)
                                      :compression compression})]
    (f)))

(def custom-registry
  {::seq-not-symbol [:sequential {:min 0 :max 10} ::not-symbol]
   ::not-symbol [:or :nil :int :string :int :double :boolean
                 :keyword :qualified-keyword :uuid]
   ::var [:re #"([a-z]|[A-Z]|-)+"]})

(defn generator [schema & {:as opts}]
  (mg/generator
   [:schema {:registry custom-registry}
    schema]
   opts))
