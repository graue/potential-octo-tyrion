(ns lobos.config
  (:require [lobos.connectivity :refer [open-global]]
            soko-api.config))

(open-global soko-api.config/dbspec)
