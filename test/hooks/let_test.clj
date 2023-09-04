(ns hooks.let-test
  (:require
   [clojure.test :refer [deftest use-fixtures is]]
   [clojure.test.check :as tc]
   [clojure.test.check.properties :as prop]
   [hooks.let]
   [hooks.test-utils :as tu]))

(use-fixtures :once tu/mock-reg-compression)

(def let->doto-equivalent-property
  (prop/for-all [atom-n (tu/generator :int)
                 fs (tu/generator [:sequential {:min 1 :max 10}
                                   [:or [:= 'inc] [:= 'dec]]])]
                (let [gs (map (fn [f] `(~'swap! ~'x ~f)) fs)
                      input (str `(~'let [~'x (~'atom ~atom-n)]
                                   ~@gs
                                   ~'x))]
                  (tap> input)
                  (tu/equivalent? hooks.let/let->doto input
                                  {:out-post deref
                                   :in-post deref}))))

(deftest let->doto-test
  (tu/test-example! #'hooks.let/let->doto)
  (is (:pass? (doto (tc/quick-check 100 let->doto-equivalent-property) tap>))))

(def let->when-let-equivalent-property
  (prop/for-all [y (tu/generator ::tu/not-symbol)
                 f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])]
                (let [input (str `(~'let [~'x ~y]
                                   (~'when ~'x (~f ~'x))))]
                  (tu/equivalent? hooks.let/let->when-let input))))

(deftest let->when-let-test
  (tu/test-example! #'hooks.let/let->when-let)
  (is (:pass? (tc/quick-check 100 let->when-let-equivalent-property))))

(def let->if-let-equivalent-property
  (prop/for-all [y (tu/generator ::tu/not-symbol)
                 z (tu/generator ::tu/not-symbol)
                 f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])]
                (let [input (str `(~'let [~'x ~y]
                                   (~'if ~'x (~f ~'x) ~z)))]
                  (tu/equivalent? hooks.let/let->if-let input))))

(deftest let->if-let-test
  (tu/test-example! #'hooks.let/let->if-let)
  (is (:pass? (tc/quick-check 100 let->if-let-equivalent-property))))

(def let->when-some-equivalent-property
  (prop/for-all [y (tu/generator ::tu/not-symbol)
                 f (tu/generator [:or
                                  [:= 'identity]
                                  [:= 'not]])]
                (let [input (str `(~'let [~'x ~y]
                                   (~'when (~'some? ~'x)
                                    (~f ~'x))))]
                  (tu/equivalent? hooks.let/let->when-some input))))

(deftest let->when-some-test
  (tu/test-example! #'hooks.let/let->when-some)
  (is (:pass? (tc/quick-check 100 let->when-some-equivalent-property))))
