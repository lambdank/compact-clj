# compact-clj
> There's no sense in being precise when you don't even know what you're talking about.
> * John von Neumann

A library that gives hints to shorten code, based on [clj-kondo](https://github.com/clj-kondo/clj-kondo).

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

## `=`
```clojure
;; = -> true?
(= x true) -> (true? x)
;; = -> nil?
(= x nil) -> (nil? x)
;; = -> empty?
(= 0 (count coll)) -> (empty? coll)
;; remove duplicates in =
(= x x y) -> (= x y)
```

## `-`
```clojure
;; - -> inc
(- n -1) -> (inc n)
;; - -> dec
(- n 1) -> (dec n)
```

## `+`
```clojure
;; remove nested ands
(+ (+ x y) z) -> (+ x y z)
;; + -> inc
(+ n 1) -> (inc n)
;; + -> dec
(+ n -1) -> (dec n)
```

## `and`
```clojure
;; remove nested ands
(and (and x y) z) -> (and x y z)
;; and -> every?
(and (f x) (f y)) -> (every? f [x y])
```

## `assoc`
```clojure
;; remove nested assocs
(assoc (assoc m :a x) :b y) -> (assoc m :a x :b y)
;; assoc -> assoc-in
(assoc m :a (assoc (:a m) :b x)) -> (assoc-in m [:a :b] x)
;; assoc -> update
(assoc m k (f (k m))) -> (update m k f)
```

## `conj`
```clojure
;; remove nested conjs
(conj (conj coll x) y) -> (conj coll x y)
```

## `filter`
```clojure
;; filter -> remove
(filter (complement f) coll) -> (remove f coll)
(filter #(not (f %)) coll) -> (remove f coll)
;; filter -> keep
(filter some? (map f coll)) -> (keep f coll) 
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
(if (not t) x y) -> (if-not t x y)
;; if -> when
(if t x nil) -> (when t x)
;; if -> boolean
(if t true false) -> (boolean t)
;; if -> not
(if t false true) -> (not t)
;; if -> cond->
(if t (f x) x) -> (cond-> x t (f))
;; move if inside
(if t (f x y) (f z y)) -> (f (if t x z) y)
;; if -> or
(if x x y) -> (or x y)
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
;; let -> if-let
(let [x y] (if x (f x) z)) -> (if-let [x y] (f x) z))
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
(not (not x)) -> (boolean x)
;; not -> not-every?
(not (every? f coll)) -> (not-every? f coll)
;; not -> seq
(not (empty? coll)) -> (seq coll)
;; not -> odd?
(not (even? n)) -> (odd? n)
;; not -> even?
(not (odd? n)) -> (even? n)
;; not -> true?
(not (false? x)) -> (true? x)
;; not -> false?
(not (true? x)) -> (false? x)
;; not -> some?
(not (nil? x)) -> (some? x)
;; not -> empty?
(not (seq coll)) -> (empty? coll)
```

## `or`
```clojure
;; remove nested ors
(or (or x y) z) -> (or x y z)
;; or -> some
(or (f x) (f y)) -> (some f [x y])
;; or -> get
(or (:a x) y) -> (:a x y)
```

## `vec`
```clojure
;; vec -> mapv
(vec (map f coll)) -> (mapv f coll)
;; vec -> filterv
(vec (filter pred coll)) -> (filterv pred coll)"
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
