# compact-clj
> There's no sense in being precise when you don't even know what you're talking about.
> * John von Neumann

A library that gives hints to shorten code, based on clj-kondo.

# Introduction
Compact-clj is a rule-based code compressor. It uses clj-kondo hooks to attach rules to `clojure.core` functions. These rules hint at how to shorten the given code. 

The rules may propose logically equivalent changes. Thus, you should be careful with compression if you require conversion to boolean. 

# Getting started
The easiest way to get started is to add this project to your `~/.clojure/deps.edn` file.
```clojure
{:deps
 {compact-clj/compact-clj {:local/root "*path-to-compact-clj*"}}}
```
You might need to ignore this in your git projects, if you are the only one using it.

# Rules
## `and`
```clojure
;; remove nested ands
(and (and x y) z) -> (and x y z)
;; and -> every?
(and (f x) (f y)) -> (every? f [x y])
```

## `assoc`
```clojure
;; assoc -> assoc-in
(assoc {:a x, b {:c y}} :b (assoc (:b {:a x, b {:c y}}) :d z)) -> (assoc-in {:a x, b {:c y}} [:b :c] z)
;; remove nested assocs
(assoc (assoc {} :a x) :b y) -> (assoc {} :a x :b y)
```

## `conj`
```clojure

;; remove nested conjs
(conj (conj [] x) y) -> (conj [] x y)
```

## `=`
```clojure
;; = -> true?
(= x true) -> (true? x)
;; = -> nil?
(= x nil) -> (nil? x) 
;; = -> empty?
(= 0 (count coll)) -> (empty? coll)
```

## `filter`
```clojure
;; filter -> remove
(filter (complement f) coll) -> (remove f coll)
(filter #(not (f %)) coll) -> (remove f coll)
;; filter -> keep
(filter identity (map f coll)) -> (keep f coll) 
```

## `first`
```clojure
;; first -> ffirst
(first (first coll)) -> (ffirst coll)
;; first -> second
(first (next coll)) -> (second coll)
```

## `if`
```clojure
;; if -> if-not
(if (not x) y z) -> (if-not x y z)
;; if -> when
(if (p x) y nil) -> (when (p x) y)
;; if -> cond->
(if (f x) (g x) x) -> (cond-> x (f x) (g))
;; move if inside
(if p (f x y) (f z y)) -> (f (if p x z) y)
```

## `into`
```clojure
;; into -> set
(into #{} [x y z]) -> (set [x y z])
;; into -> vec
(into [] '(x y z)) -> (vec '(x y z))
```

## `let`
```clojure
;; let -> doto
(let [x y] (f x) (g x) x) -> (doto y (f) (g))
;; let -> when-let
(let [x y] (when x (f x))) -> (when-let [x y] (f x))
```

## `map`
```clojure
;; remove nested maps
(map f (map g coll)) -> (map (comp f g) coll)
```

## `not`
```clojure
;; not -> not=
(not (= x y)) -> (not= x y)
;; not -> not-any?
(not (some f coll)) -> (not-any? f coll)
;; not -> identity
(not (not x)) -> x
;; not -> not-every?
(not (every? f coll)) -> (not-every? f coll)
;; not -> seq
(not (empty? coll)) -shorten-> (seq coll)
;; not -> odd?
(not (even? x)) -> (odd? x)
;; not -> even?
(not (odd? x)) -> (even? x)
;; not -> true?
(not (false? x)) -> (true? x)
;; not -> false?
(not (true? x)) -> (false? x)
;; not -> empty?
(not (seq coll)) -> (empty? coll)
```

## `or`
```clojure
;; or -> some
(or (f x) (f y)) -> (some f [x y])
;; or -> get
(or (:a x) y) -> (:a x y)
```

## `vec`
```clojure
;; vec -> mapv
(vec (map f coll)) -> (mapv f coll)
```

## `when`
```clojure
;; when -> when-not
(when (not x) y) -> (when-not x y)
;; when -> not-empty
(when (seq coll) coll) -> (not-empty coll)
```

## `when-let`
```clojure
;; when-let -> when-first
(when-let [x (first xs)] (f x)) -> (when-first [x xs] (f x))
```
