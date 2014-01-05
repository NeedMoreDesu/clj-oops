(ns clj-oops.object
 (:use [clojure.core.match :only (match)])
 (:use clj-oops.meta))

(defn obj? [arg]
 (= ::custom-object (:type (metadata arg))))
(defn obj-type [arg]
 (:custom-object (metadata arg)))
(defn obj-inheritance-list [arg]
 (:inheritance-list (metadata arg)))
(defn obj-fields [arg]
 (:field-map (metadata arg)))

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
        (into {} field-map))
       inheritance-list
       (if (obj? basic-object)
        (cons
         (obj-type basic-object)
         (obj-inheritance-list basic-object)))
       meta
       {:type ::custom-object
        :custom-object class-name
        :field-map field-map
        :inheritance-list inheritance-list}
       basic-object (unintern basic-object)]
  (metadata
   basic-object
   meta)))

(defn obj-copy
 "Shallow copy"
 [object & {:keys [field-map basic-object]}]
 (let [field-map
       (into (obj-fields object) field-map)
       basic-object
       (or basic-object object)
       meta (assoc (metadata object)
             :field-map field-map)]
  (metadata
   basic-object
   meta)))
