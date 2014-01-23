(ns lobos.helpers
  (:require [lobos.schema :as s]))

(defn surrogate-key [table]
  (s/integer table :id :auto-inc :primary-key))

(defn timestamps [table]
  (-> table
      (s/timestamp :updated_on)
      (s/timestamp :created_on (s/default (now)))))

(defn refer-to [table foreign-table]
  (let [column-name (->> foreign-table name butlast (apply str)
                         (#(str % "_id"))
                         keyword)]
    (s/integer table column-name [:refer foreign-table
                                  :id, :on-delete, :set-null])))

(defmacro tbl [name & elements]
  `(-> (s/table ~name)
       (timestamps)
       ~@(reverse elements)
       (surrogate-key)))
