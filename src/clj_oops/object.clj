(ns clj-oops.object
 (:use [clojure.core.match :only (match)]))

(defn obj? [arg]
 (= ::custom-object (type arg)))
(defn obj-type [arg]
 (:custom-object (meta arg)))
(defn obj-inheritance-list [arg]
 (:inheritance-list (meta arg)))
(defn obj-fields [arg]
 (:field-map (meta arg)))
(def obj-basic :basic)

(defn object-input-parse-internal [arg args]
 (match [args]
  [([([(fn-fn :guard ifn?)
       & fn-args] :seq)
     & new-args] :seq)]
  (if (and (obj? arg) (keyword? fn-fn))
   (recur (apply (fn-fn (obj-fields arg)) fn-args) new-args)
   (recur (apply fn-fn arg fn-args) new-args))
  [([(fn-fn :guard ifn?)
     & new-args] :seq)]
  (if (and (obj? arg) (keyword? fn-fn))
   (recur (fn-fn (obj-fields arg)) new-args)
   (recur (fn-fn arg) new-args))
  [([] :seq)]
  arg))

(defn obj-new [class-name basic-object & [field-map]]
 (let [field-map
       (if (obj? basic-object)
        (into (obj-fields basic-object) field-map)
        (into {:basic basic-object} field-map))
       inheritance-list
       (if (obj? basic-object)
        (cons
         (:custom-object (meta basic-object))
         (:inheritance-list (meta basic-object))))
       meta
       {:type ::custom-object
        :custom-object class-name
        :field-map field-map
        :inheritance-list inheritance-list}]
 (with-meta
  (fn self [& args]
   (object-input-parse-internal
    (with-meta self meta)
    args))
  meta)))

(defn obj-copy [object & [field-map]]
 (let [field-map
       (into (obj-fields object) field-map)
       meta (assoc (meta object)
             :field-map field-map)] 
  (with-meta
   (fn self [& args]
    (object-input-parse-internal
     (with-meta self meta)
     args))
   meta)))

(defmethod print-method ::custom-object [o w]
 (print-simple
  (str
   "#<obj "
   (obj-type o)
   " "
   (obj-fields o)
   ">") w))
