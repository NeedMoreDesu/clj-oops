(ns clj-oops.meta
 (:use [clojure.core.match :only (match)])
 (:import java.util.IdentityHashMap))

(def meta-map (ref (IdentityHashMap.)))

(defn unintern
 "-128 to 127 are interned,
  string are interned.
  Keywords are allways interned, can't unintern."
 [arg]
 (let [long java.lang.Long
       string java.lang.String
       keyword clojure.lang.Keyword]
  (match (type arg)
   long (Long. arg)
   string (String. arg)
   keyword (throw (Throwable. "keywords cannot be uninterned"))
   _ arg)))

(defn meta-key-encode [arg]
 (System/identityHashCode arg))

(defn metadata
 ([item meta]
  (dosync (.put @meta-map item meta))
  item)
 ([item] (.get @meta-map item)))

