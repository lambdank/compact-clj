(ns hooks.conj-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.conj]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def conj-remove-nested-equivalent-property
  (prop/for-all
   [coll (tu/generator [:sequential {:min 2 :max 10} ::tu/not-symbol])
    xs (tu/generator [:sequential {:min 1 :max 10} ::tu/not-symbol])
    ys (tu/generator [:sequential {:min 1 :max 10} ::tu/not-symbol])]
   (let [input (str `(~'conj (~'conj ~coll ~@xs) ~@ys))]
     (tu/equivalent? hooks.conj/conj-remove-nested input
                     {:out-pre #(str "(conj " % " "
                                     (->> (map str (:children (api/parse-string (str (vec ys)))))
                                          (map str)
                                          (str/join " ")) ")")}))))

(deftest conj-remove-nested-test
  (is (= {:root-node "(conj coll x)"
          :compression "coll x"
          :type :compact-clj/conj-remove-nested}
         (-> #'hooks.conj/conj-remove-nested meta :example :in str api/parse-string hooks.conj/conj-remove-nested (dissoc :highlight-node))))
  (is (:pass? (tc/quick-check 100 conj-remove-nested-equivalent-property))))
