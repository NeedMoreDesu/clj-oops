(ns test.clj-oops.multi-test
 (:use clj-oops.multi)
 (:use clj-oops.object)
 (:use clj-oops.meta))

(def n1 (obj-new :num 3 {:x "qwe"}))
(def n2 (obj-new :num2 n1 {:x "asd"}))
(def n3 (obj-new :num3 10 {:y "zxc"}))

(obj? n3)
;; => true
(obj? 10)
;; => false

(let [hello
      (mfn)]
 (add-fn hello java.lang.Long
  (fn [arg]
   (str
    "hello from number " arg)))
 (add-fn hello {:custom-class :num}
  (fn [arg]
   (str
    "hello from obj " arg " "
    "with field x = " (-> arg obj-fields :x))))
 (def hello hello))
;; => #'test.clj-oops.multi-test/hello

(hello 2)
;; => "hello from number 2"
(hello n1)
;; => "hello from obj 3 with field x = qwe"
(hello n2)
;; => "hello from obj 3 with field x = asd"
(hello n3)
;; => "hello from number 10"

(+ n1 (/ n2 n3) 3)
;; => 63/10
