(ns hospital.class3
  (:use clojure.pprint)
  (:require [schema.core :as s]))

(s/set-fn-validation! true)

(def PosInt (s/pred pos-int?))
(def Patient {:id PosInt, :name s/Str})

(s/defn new-patient :- Patient
  [id   :- PosInt
   name :- s/Str]
  { :id id :name name })

(defn bigger-or-equal-zero? [x] (<= x 0))
(def FinancialValue (s/constrained s/Num bigger-or-equal-zero?))
(def Medical-Request
  {:patient Patient
   :value FinancialValue
   :prodecure s/Keyword})
(s/defn new-medical-request :- Medical-Request
  [patient   :- Patient
   value     :- FinancialValue
   procedure :- s/Keyword]
  {:patient patient, :value value, :procedure procedure})

(def Health-Plan [s/Keyword])
(def Patient-P {:id PosInt, :name s/Str, :plan Health-Plan})

(pprint (s/validate Patient-P {:id 15 :name "Babana" :plan [:raio-x]}))
(pprint (s/validate Patient-P {:id 15 :name "Babana" :plan nil}))