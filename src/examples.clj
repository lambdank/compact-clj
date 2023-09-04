(ns examples)

(declare x y z n m k t f g h xs coll pred)

(= x true)
(= x nil)
(= 0 (count coll))
(= x x y)
(= (= x y) z)

(- n -1)
(- n 1)

(+ (+ x y) z)
(+ n 1)
(+ n -1)

(and (and x y) z)
(and (f x) (f y))

(assoc (assoc m :a x) :b y)
(assoc m :a (assoc (:a m) :b x))
(assoc m k (f (k m) x y z))

(comp (comp f g) h)

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

(max (max x y) z)

(min (min x y) z)

(not (= x y))
(not (some f coll))
(not (not x))
(not (every? f coll))
(not (empty? coll))
(not (even? n))
(not (odd? n))
(not (nil? x))
(not (seq coll))

(or (f x) (f y))
(or (:a x) y)
(or (or x y) z)

(vec (map f coll))
(vec (filter pred coll))

(when (not x) y)
(when (seq coll) coll)

(when-let [x (first xs)] (f x))

(when-some [x y] (f x))
