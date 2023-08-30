(ns hooks.test-utils
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [is]]
   [hooks.utils :as u]))

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


(defn mock-reg-compression [f]
  (with-redefs [u/reg-compression! (fn [tipe root-node highlight-node compression]
                                     {:type tipe
                                      :root-node (str root-node)
                                      :highlight-node (str highlight-node)
                                      :compression compression})]
    (f)))

