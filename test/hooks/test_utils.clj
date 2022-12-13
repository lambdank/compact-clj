(ns hooks.test-utils
  (:require
   [clj-kondo.hooks-api :as api]))

(defn mock-reg-finding [f]
  (with-redefs [api/reg-finding! identity]
    (f)))
