(ns hospital.class4
  (:use clojure.pprint)
  (:require [schema.core :as s]))

(s/set-fn-validation! true)

(def PosInt (s/pred pos-int?))
(def Health-Plan [s/Keyword])
(def Patient
  {:id PosInt,
   :name s/Str,
   :plan Health-Plan,
   (s/optional-key :birth-date) s/Str})

;dynamic maps
(def Patients
  {PosInt Patient})

(def Visits
  {PosInt [s/Str]})

