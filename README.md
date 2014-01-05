# clj-oops

Oops, it has been created. :)

OOP-System for functional language. Raw raw. Fight da power!



Is it mutable?

No. If you want field to be mutable, make that field ref or atom.

Is it thread-safe?

I think no, but it wasn't tested.

Is it memory-safe?

Nope. Every object's metadata stored in IdentityHashMap, which holds strong reference to the object and it's metadata. So objects aren't gc'd.

Why would I even use it if it is that bad?

It looks cool. You can make objects from basic types and use them as basic types, but with methods and fields. Or play with polymorphism and inheritance.

## Usage

```clojure
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
 (add-fn hello :default
  (fn [arg]
   "umm.. hi?"))
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
(hello :asd)
;; => "umm.. hi?"

(default-dispatch-fn 2)
;; => (java.lang.Long
;;     :default)
(default-dispatch-fn n2)
;; => ({:custom-class :num2}
;;     {:custom-class :num}
;;     :custom-class
;;     java.lang.Long
;;     :default)

(+ n1 (/ n2 n3) 3)
;; => 63/10
```

## License

Copyright © 2014 NeedMoreDesu desu@horishniy.org.ua

This program is free software. It comes without any warranty, to
the extent permitted by applicable law. You can redistribute it
and/or modify it under the terms of the Do What The Fuck You Want
To Public License, Version 2, as published by Sam Hocevar. See
COPYING for more details.
