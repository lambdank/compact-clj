(ns test)

(into [] [1 2 3])

(assoc {} :a (assoc (:a {}) :b 2))

(assoc {} :a (assoc ({} :a) :b 2))

(= 2 true)

(not (= 2 3))

(not (some even? [1 2 3]))
