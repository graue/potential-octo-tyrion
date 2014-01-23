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
