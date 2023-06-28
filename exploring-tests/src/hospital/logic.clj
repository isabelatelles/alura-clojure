(ns hospital.logic
  (:require [schema.core :as s]
            [hospital.model :as h.model]))

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
    (throw (ex-info "It does not fit in line."
                    {:patient person-id, :type :impossible-to-fit-in-line}))))

(s/defn serves-patient :- h.model/Hospital
  [hospital :- h.model/Hospital
   department :- s/Keyword]
  (update hospital department pop))

(s/defn next-patient :- h.model/Patient-Id
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
  ; sometimes there are conditions that we dont want to check in schema, but
  ; instead in the function itself
  {:pre [(contains? hospital department-from), (contains? hospital department-to)]
   :post [(same-size? hospital % department-from department-to)]}
  (let [person-id (next-patient hospital department-from)]
    (-> hospital
        (serves-patient department-from)
        (arrives-at department-to person-id))))

(defn arrives-at-internal-
  [hospital department person-id]
  (when (fits-in-line? hospital department)
    (update hospital department conj person-id)))

(defn arrives-at-public
  [hospital department person-id]
  (if-let [new-hospital (arrives-at-internal- hospital department person-id)]
    { :hospital new-hospital, :result :success }
    { :hospital hospital, :result :impossible-to-fit-in-line}))