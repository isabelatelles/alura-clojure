(ns hospital.model
  (:require [schema.core :as s]))

(def empty-line clojure.lang.PersistentQueue/EMPTY)

(s/def Patient-Id s/Str)
(s/def Department (s/queue Patient-Id))
(s/def Hospital {s/Keyword Department})