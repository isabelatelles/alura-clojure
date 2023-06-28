(ns hospital.class5)

(defn authorizator-type
  [medical-order]
  (let [patient (:patient medical-order)
        situation (:situation patient)]
    (cond (= :emergency situation) :always-authorize
          (contains? patient :plan) :health-plan
          :else :minimal-credit)))

(defmulti should-sign-pre-authorization? authorizator-type)

(defmethod should-sign-pre-authorization? :always-authorize
  [medical-order]
  false)

(defmethod should-sign-pre-authorization? :health-plan
  [medical-order]
  (let [health-plan (get-in medical-order [:plan :patient])
        procedure (:procedure medical-order)]
    (not (some #(= % procedure) health-plan))))

(defmethod should-sign-pre-authorization? :minimal-credit
  [medical-order]
  (>= (:value medical-order) 50))

(let [particular-patient {:id 15 :name "isabela" :date-of-birth "05/02/2000" :situation :emergency}
      health-plan-patient {:id 15 :name "isabela" :date-of-birth "05/02/2000" :situation :emergency :plan [:x-ray :ultrasound]}]
  (println (should-sign-pre-authorization? {:patient particular-patient :procedure :x-ray :value 500}))
  (println (should-sign-pre-authorization? {:patient health-plan-patient :procedure :x-ray :value 40})))

(let [particular-patient {:id 15 :name "isabela" :date-of-birth "05/02/2000" :situation :normal}
      health-plan-patient {:id 15 :name "isabela" :date-of-birth "05/02/2000" :situation :normal :plan [:x-ray :ultrasound]}]
  (println (should-sign-pre-authorization? {:patient particular-patient :procedure :x-ray :value 1000}))
  (println (should-sign-pre-authorization? {:patient health-plan-patient :procedure :blood :value 1000})))