(ns hooks.test-utils
  (:require
   [clj-kondo.hooks-api :as api]
   [clojure.test :refer [is]]
   [hooks.utils :as u]))

(defn test-example!
  "Tests the rule using `in` and `out`.
  Uses metadata example of rule if `in` and `out` are not provided provided."
  [var-object {:keys [col end-col
                      row end-row
                      in out]
               :or {row 1
                    end-row 1
                    in (-> var-object meta :example :in)
                    out (-> var-object meta :example :out)}}]
  (is (= {:row row
          :end-row end-row
          :col col
          :end-col end-col
          :message (u/->msg (str in) (str out))
          :type (-> var-object meta :type)}
         ((deref var-object) (api/parse-string (str in))))))

(defn mock-reg-finding [f]
  (with-redefs [api/reg-finding! identity]
    (f)))
