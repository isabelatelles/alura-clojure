(ns hospital.logic
  (:require [schema.core :as s]
            [hospital.model :as h.model]))

(defn fits-in-line-deprecated?
  [hospital
   department]
  (some-> hospital
          department
          count
          (< 5)))

(s/defn fits-in-line?
  [hospital :- h.model/Hospital
   department :- s/Keyword]
  (some-> hospital
          department
          count
          (< 5)))

(s/defn arrives-at :- h.model/Hospital
  [hospital :- h.model/Hospital
   department :- s/Keyword
   person-id :- h.model/Patient-Id]
  (if (fits-in-line? hospital department)
    (update hospital department conj person-id)
    (throw (java.lang.IllegalStateException. "It does not fit in line."))))
    ;(throw (ex-info "It does not fit in line."
                    ;{:patient person-id, :type :impossible-to-fit-in-line}

(s/defn serves-patient :- h.model/Hospital
  [hospital :- h.model/Hospital
   department :- s/Keyword]
  (update hospital department pop))

(s/defn next-patient :- (s/maybe h.model/Patient-Id)
  [hospital :- h.model/Hospital
   department :- s/Keyword]
  (-> hospital
      department
      peek))

(defn same-size?
  [hospital another-hospital department-from department-to]
  (= (+ (count (get another-hospital department-from)) (count (get another-hospital department-to)))
     (+ (count (get hospital department-from)) (count (get hospital department-to)))))

(s/defn transfer :- h.model/Hospital
  [hospital :- h.model/Hospital
   department-from :- s/Keyword
   department-to :- s/Keyword]
  {:pre [(contains? hospital department-from), (contains? hospital department-to)]
   :post [(same-size? hospital % department-from department-to)]}
  (if-let [person-id (next-patient hospital department-from)]
    (-> hospital
        (serves-patient department-from)
        (arrives-at department-to person-id))
    hospital))

(s/defn total-patients :- s/Int
  [hospital :- h.model/Hospital]
  (->> hospital
      vals
      (map count)
      (reduce +)))