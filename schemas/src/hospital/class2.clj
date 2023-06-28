(ns hospital.class2
  (:use clojure.pprint)
  (:require [schema.core :as s]))

(s/set-fn-validation! true)

(defn strictly-positive?[x] (< x 0))
(def Strictly-Positive (s/pred strictly-positive?))

(def Patient
  "Schema of patient"
  {:id (s/constrained s/Int strictly-positive?), :name s/Str})
; we can switch by pos-int?

(s/defn new-patient :- Patient
  [id :- s/Num name :- s/Str]
  { :id id :name name })