(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:require [lobos.migration :refer [defmigration]]
            [lobos.core :refer :all]
            [lobos.schema :refer :all]
            [lobos.config :refer :all]
            [lobos.helpers :refer :all]))

(defmigration add-tokens-table
  (up [] (create
           (table :tokens
                  (text :email)
                  (text :token :primary-key))))
  (down [] (drop (table :tokens))))

(defmigration add-users-and-levels
  (up []
      (create
        (table :users
               (text :email :unique)
               (integer :id :primary-key :auto-inc)))
      (create
        (table :levels
               (integer :id :primary-key :auto-inc)
               (text :name :unique)))
      (create
        (table :user_levels
               (integer :id :primary-key :auto-inc)
               (integer :score)
               (text :level)
               (refer-to :users)
               (refer-to :levels))))
  (down []
        (drop (table :user_levels))
        (drop (table :users))
        (drop (table :levels))))
