(ns hospital.class4
  (:use clojure.pprint))

(defrecord ParticularPatient [id name date-of-birth situation])
(defrecord HealthPlanPatient [id name date-of-birth situation plan])

(defprotocol Chargeable
  (should-sign-pre-authorization? [patient procedure value]))

(defn not-emergency?
  [patient]
  (not= :emergency (:situation patient :normal)))

(extend-type ParticularPatient
  Chargeable
  (should-sign-pre-authorization? [patient procedure value]
    (and (>= value 50)
         (not-emergency? patient))))

(extend-type HealthPlanPatient
  Chargeable
  (should-sign-pre-authorization? [patient procedure value]
    (let [plan (:plan patient)]
      (and (not (some #(= % procedure) plan))
           (not-emergency? patient)))))

(let [particular-patient (->ParticularPatient 15 "isabela" "05/02/2000" :normal)
      health-plan-patient (->HealthPlanPatient 15 "isabela" "05/02/2000" :normal [:x-ray :ultrasound])]
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 500))
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :blood 40)))

(let [particular-patient (->ParticularPatient 15 "isabela" "05/02/2000" :emergency)
      health-plan-patient (->HealthPlanPatient 15 "isabela" "05/02/2000" :emergency [:x-ray :ultrasound])]
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 500))
  (pprint (should-sign-pre-authorization? particular-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :x-ray 40))
  (pprint (should-sign-pre-authorization? health-plan-patient :blood 40)))


(defn authorizator-type
  [medical-order]
  (let [patient (:patient medical-order)
        situation (:situation patient)
        emergency? (= :emergency situation)]
    (if emergency?
      :always-authorize
      (class patient))))

(defmulti should-sign-pre-authorization-of-medical-order? authorizator-type)

(defmethod should-sign-pre-authorization-of-medical-order? :always-authorize
  [medical-order]
  false)

(defmethod should-sign-pre-authorization-of-medical-order? ParticularPatient
  [medical-order]
  (>= (:value medical-order) 50))

(defmethod should-sign-pre-authorization-of-medical-order? HealthPlanPatient
  [medical-order]
  (let [health-plan (get-in medical-order [:plan :patient])
        procedure (:procedure medical-order)]
    (not (some #(= % procedure) health-plan))))

(let [particular-patient (->ParticularPatient 15 "isabela" "05/02/2000" :emergency)
      health-plan-patient (->HealthPlanPatient 15 "isabela" "05/02/2000" :emergency [:x-ray :ultrasound])]
  (pprint (should-sign-pre-authorization-of-medical-order? {:patient particular-patient :procedure :x-ray :value 500}))
  (pprint (should-sign-pre-authorization-of-medical-order? {:patient health-plan-patient :procedure :x-ray :value 40})))

(let [particular-patient (->ParticularPatient 15 "isabela" "05/02/2000" :normal)
      health-plan-patient (->HealthPlanPatient 15 "isabela" "05/02/2000" :normal [:x-ray :ultrasound])]
  (pprint (should-sign-pre-authorization-of-medical-order? {:patient particular-patient :procedure :x-ray :value 1000}))
  (pprint (should-sign-pre-authorization-of-medical-order? {:patient health-plan-patient :procedure :blood :value 1000})))