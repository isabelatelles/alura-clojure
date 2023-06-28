(ns hospital.logic
  (:require [hospital.model :as h.model]))

(defn fits-in-line?
  [hospital department]
  (-> hospital
      (get department)
      count
      (< 5)))

(defn arrives-at
  [hospital department person]
  (if (fits-in-line? hospital department)
    (update hospital department conj person)
    (throw (ex-info "Line is full." {:person person}))))

(defn arrives-at-paused-logging
  [hospital department person]
  (println "Trying to add" person)
  (Thread/sleep (* (rand) 2000))
  (if (fits-in-line? hospital department)
    (do
      (println "Updating" person)
      (update hospital department conj person))
    (throw (ex-info "Line is full." {:person person}))))

(defn serves-patient
  [hospital department]
  (update hospital department pop))

(defn next
  [hospital department]
  (-> hospital
      department
      peek))

(defn next-with-juxt
  [hospital department]
  (let [line (get hospital department)
        peek-pop (juxt peek pop)
        [person updated-line] (peek-pop line)
        updated-hospital (update hospital assoc department updated-line)]
    {:patient person
     :hospital updated-hospital}))

(defn transfer
  [hospital from to]
  (let [person (next hospital from)]                        ; as a pure function we dont need to worry if next person will change because of immutability
    (-> hospital
        (serves-patient from)
        (arrives-at to person))))