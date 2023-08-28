(ns examples)

(declare x y z n m t f g xs coll pred)

(= x true)
(= x nil)
(= 0 (count coll))
(= x x y)

(- n -1)
(- n 1)

(+ (+ x y) z)
(+ n 1)
(+ n -1)

(and (and x y) z)
(and (f x) (f y))

(assoc (assoc m :a x) :b y)
(assoc m :a (assoc (:a m) :b x))

(conj (conj coll x) y)

(filter (complement f) coll)
(filter #(not (f %)) coll)

(first (first coll))
(first (next coll))

(if (not t) x y)
(if t x nil)
(if t true false)
(if t false true)
(if t (f x) x)
(if t (f x y) (f z y))
(if x x y)

(into #{} [x y z])
(into [] '(x y z))

(let [x y] (f x) (g x) x)
(let [x y] (when x (f x)))
(let [x y] (if x (f x) z))

(map f (map g coll))

(not (= x y))
(not (some f coll))
(not (not x))
(not (every? f coll))
(not (empty? coll))
(not (even? n))
(not (odd? n))
(not (false? x))
(not (true? x))
(not (nil? x))
(not (seq coll))

(or (f x) (f y))
(or (:a x) y)

(vec (map f coll))
(vec (filter pred coll))

(when (not x) y)
(when (seq coll) coll)
(when-let [x (first xs)] (f x))
