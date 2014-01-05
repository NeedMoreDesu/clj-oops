(ns clj-oops.multi
 (:use [clojure.core.match :only (match)]))
(use 'clj-oops.object)

(defn default-dispatch-fn [arg]
 `(~@(if (obj? arg)
     (conj
      (vec
       (map
        (fn [custom-class]
         {:custom-class custom-class})
        (conj
         (obj-inheritance-list arg)
         (obj-type arg))))
      :custom-class))
   ~(type arg)
   :default))

(defn mfn [& {:keys [multimap-ref default dispatch-fn]}]
 (let [multimap-ref
       (or multimap-ref (ref {}))
       dispatch-fn
       (or dispatch-fn
        default-dispatch-fn)
       result-fn
       (fn [& args]
        (if args
         (let [[arg & args]
               args
               f
               (some
                #(get @multimap-ref %)
                (dispatch-fn arg))]
          (if f
           (apply f arg args)
           (throw (Throwable.
                   (str
                    "No function matches type "
                    (or (type arg) "nil")
                    (if (obj? arg)
                     (str
                      " of custom-class"
                      (obj-type arg)) ""))))))
         (let [f (some
                  #(get @multimap-ref %)
                  [:no-args :default])]
          (if f
           (f)
           (throw (Throwable.
                   "No :no-args or :default fn"))))))]
  (if default
   (dosync
    (alter multimap-ref
     assoc :default default)))
  (with-meta result-fn
   (assoc (meta result-fn)
    :type ::multi
    :multimap-ref multimap-ref
    :dispatch-fn dispatch-fn))))
(defn mfn? [arg]
 (= ::multi (type arg)))

(defn mfn-multimap [multifn]
 (:multimap-ref (meta multifn)))

(defn add-fn [multifn key fn]
 (dosync (alter (mfn-multimap multifn) assoc
          key
          fn))
 multifn)
(defn remove-fn [multifn key]
 (dosync (alter (mfn-multimap multifn) dissoc key)))

(defmethod print-method ::multi [o w]
 (print-simple
  (str
   "#<multifn for "
   (clojure.string/join ", "
    (keys @(:multimap-ref (meta o))))
   ">") w))
