(ns test.clj-oops.multi-test
 (:use clj-oops.multi)
 (:use clj-oops.object)
 (:refer-clojure :exclude [+]))

(def n1 (obj-new :num 3))
(def n2 (obj-new :num2 n1 {:x "asd"}))

(let [to-number
      (mfn
       :dispatch-fn
       (fn [arg]
        (if (number? arg)
         [:number :default]
         (default-dispatch-fn arg))))]
 (add-fn to-number :number identity)
 (add-fn to-number {:custom-class :num} #(% :basic))
 (def to-number to-number))

(let [add (mfn)]
 (add-fn add :default
  (fn [& args]
   (apply clojure.core/+ (map to-number args))))
 (add-fn add {:custom-class :num}
  (fn [arg & args]     ; can be sure that have atleast 1 arg
   (obj-copy arg
    {:basic
     (arg
      :basic
      (fn [arg]
       (apply add arg args)))})))
 (def + add))

(+ 1 2 n1)
;; => 6
(+ n1 2 3)
;; => #<obj :num {:basic 8}>
(+ n2 n1 -1)
;; => #<obj :num2 {:basic 5, :x "asd"}>
(def n3 (n1 [+ 1] [+ n2] [+ 2]))
n3
;; => #<obj :num {:basic 9}>
(n3 [+ 1000])
;; => #<obj :num {:basic 1009}>
((+ n2 10) [+ 200])
;; => #<obj :num2 {:basic 213, :x "asd"}>

