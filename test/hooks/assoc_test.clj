(ns hooks.assoc-test
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.string :as str]
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.assoc]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def assoc-remove-nested-equivalent-property
  (prop/for-all
   [m (tu/generator [:map])
    xs (tu/generator [:sequential {:min 2 :max 10} ::tu/not-symbol])
    ys (tu/generator [:sequential {:min 2 :max 10} ::tu/not-symbol])]
   (let [even-xs (cond->> xs (odd? (count xs)) (drop 1))
         even-ys (cond->> ys (odd? (count ys)) (drop 1))
         input (str `(~'assoc (~'assoc ~m ~@even-xs) ~@even-ys))]
     (tu/equivalent? hooks.assoc/assoc-remove-nested input
                     {:out-pre #(str "(assoc " % " "
                                     (->> (map str (:children (api/parse-string (str (vec even-ys)))))
                                          (map str)
                                          (str/join " ")) ")")}))))

(deftest assoc-remove-nested-test
  (is (= {:root-node "(assoc m :a x)"
          :compression "m :a x"
          :type :compact-clj/assoc-remove-nested}
         (-> #'hooks.assoc/assoc-remove-nested meta :example :in str api/parse-string hooks.assoc/assoc-remove-nested (dissoc :highlight-node))))
  (is (:pass? (tc/quick-check 100 assoc-remove-nested-equivalent-property))))

(def assoc->assoc-in-equivalent-property
  (prop/for-all [m (tu/generator [:map])
                 k1 (tu/generator ::tu/not-symbol)
                 k2 (tu/generator ::tu/not-symbol)
                 v (tu/generator ::tu/not-symbol)]
                (let [input (str `(~'assoc ~m ~k1 (~'assoc (~'get ~m ~k1) ~k2 ~v)))]
                  (tu/equivalent? hooks.assoc/assoc->assoc-in input))))

(deftest assoc->assoc-in-test
  (tu/test-example! #'hooks.assoc/assoc->assoc-in)
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:in '(assoc m :a (assoc (m :a) :b x))})
  (tu/test-example! #'hooks.assoc/assoc->assoc-in {:in '(assoc m :a (assoc (get m :a) :b x))})
  (is (:pass? (tc/quick-check 100 assoc->assoc-in-equivalent-property))))

(def assoc->update-equivalent-property
  (prop/for-all [m (tu/generator [:map])
                 k (tu/generator ::tu/not-symbol)
                 f-args (tu/generator ::tu/seq-not-symbol)]
                (let [f `(fn [& ~'args] ~'args)
                      input (str `(~'assoc ~m ~k (~f (~'get ~m ~k) ~@f-args)))]
                  (tu/equivalent? hooks.assoc/assoc->update input))))

(deftest assoc->update-test
  (tu/test-example! #'hooks.assoc/assoc->update)
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (get m k)))})
  (tu/test-example! #'hooks.assoc/assoc->update {:in '(assoc m k (f (m k) x y z))
                                                 :out '(update m k f x y z)})
  (is (:pass? (tc/quick-check 100 assoc->update-equivalent-property))))
