(ns hospital.class2
  (:use clojure.pprint))

(defrecord ParticularPatient [id name date-of-birth])
(defrecord HealthPlanPatient [id name date-of-birth plan])

;similar to interface in Java
(defprotocol Chargeable
  (should-sign-pre-authorization? [patient procedure value]))

(extend-type ParticularPatient
  Chargeable
  (should-sign-pre-authorization? [patient procedure value]
    (>= value 50)))

; (defrecord ParticularPatient
;   [id name date-of-birth]
;   Chargeable
;   (should-sign-pre-authorization? [patient procedure value]
;     (>= value 50)))

(extend-type HealthPlanPatient
  Chargeable
  (should-sign-pre-authorization? [patient procedure value]
    (let [plan (:plan patient)]
      (not (some #(= % procedure) plan)))))

(let [particular-patient (->ParticularPatient 15 "isabela" "05/02/2000")
      health-plan-patient (->HealthPlanPatient 15 "isabela" "05/02/2000" [:x-ray :ultrasound])]
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 500))
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :blood 40)))