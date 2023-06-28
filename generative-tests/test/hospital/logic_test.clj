(ns hospital.logic-test
  (:use clojure.pprint)
  (:require [clojure.test :refer :all]
            [hospital.logic :refer :all]
            [schema.core :as s]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [hospital.model :as h.model]
            [schema-generators.generators :as g]))

(s/set-fn-validation! true)

(deftest fits-in-line-deprecated?-test
  (testing "It fits in line when size of line is until 4 inclusive"
    ; with more than one symbol this does not work well because it will re-use symbols generated
    (doseq [line (gen/sample (gen/vector gen/string-alphanumeric 0 4) 10)]
      (is (fits-in-line-deprecated? {:wait line}, :wait))))

  ; boundary zero
  (testing "It fits in line when line is empty"
    (is (fits-in-line-deprecated? {:wait []} :wait)))
  ; boundary limit
  (testing "It does not fit in line when line is full"
    (is (not (fits-in-line-deprecated? {:wait [1 2 3 4 5]} :wait))))
  ; boundary above limit
  (testing "It does not fit in line when line is more than full"
    (is (not (fits-in-line-deprecated? {:wait [1 2 3 4 5 6]} :wait))))
  ; inside boundary
  (testing "It fits in line when line is not full"
    (is (fits-in-line-deprecated? {:wait [1 2 3 4]} :wait))
    (is (fits-in-line-deprecated? {:wait [1 2]} :wait)))
  ; boundary department
  (testing "It does not fit in line when department does not exist"
    (is (not (fits-in-line-deprecated? {:wait []} :w)))))

; property-based tests will not test the expected value the function returns,
; i.e. it won't test success or failure of the function,
; rather it will test properties of a certain function

(defn transform-vector-in-line
  [vector]
  (reduce conj h.model/empty-line vector))

(def random-name-gen (gen/fmap clojure.string/join
                               (gen/vector gen/char-alphanumeric 5 10)))
(def not-full-line-gen (gen/fmap transform-vector-in-line
                                 (gen/vector random-name-gen 0 4)))
;(defn transfer-ignoring-exceptions
  ;[hospital to-line]
  ;(try
    ;(transfer hospital :wait to-line)
    ;(catch clojure.lang.ExceptionInfo e
        ;(println "didnt work")
        ;        hospital)))

(defn transfer-ignoring-exceptions
  [hospital to-line]
  (try
    (transfer hospital :wait to-line)
     (catch java.lang.IllegalStateException e
       hospital)))
(defspec transfer-has-to-maintain-total-patients-in-hospital 50
         (prop/for-all
           [wait-line not-full-line-gen
            x-ray-line not-full-line-gen
            ultrasound-line not-full-line-gen
            go-to-line (gen/vector (gen/elements [:x-ray :ultrasound]) 10 50)]
           (let [initial-hospital {:wait wait-line :x-ray x-ray-line :ultrasound ultrasound-line}
                 final-hospital (reduce transfer-ignoring-exceptions initial-hospital go-to-line)] ; does a lot of transfers and only sticks to last final-hospital
             (println (count (:wait initial-hospital)) (count (:x-ray initial-hospital)) (count (:ultrasound initial-hospital)))
             (is (= (total-patients initial-hospital)
                    (total-patients final-hospital))))))

;---------------------
(defn add-wait-line
  [[hospital line]]
  (assoc hospital :wait line))

(def hospital-gen
  (gen/fmap
    add-wait-line
    (gen/tuple (gen/not-empty (g/generator h.model/Hospital))
               not-full-line-gen)))

(defn non-existent-department
  [department]
  (keyword (str department "-non-existent")))

(defn transfer-gen
  [hospital]
  (let [existent-departments (keys hospital)
        non-existent-departments (map non-existent-department existent-departments)
        all-departments (concat existent-departments non-existent-departments)]
    (gen/tuple (gen/return transfer)
               (gen/elements all-departments)
               (gen/elements all-departments)
               (gen/return 0))))

(def arrives-at-gen
  (gen/tuple (gen/return arrives-at)
             (gen/return :wait)
             random-name-gen
             (gen/return 1)))

(defn action-gen
  [hospital]
  (gen/one-of [arrives-at-gen (transfer-gen hospital)]))

(defn actions-gen
  [hospital]
  (gen/not-empty (gen/vector (action-gen hospital) 1 100)))

(defn executes-an-action
  [situation [function param1 param2 param3]]
  (let [hospital (:hospital situation)
        current-delta (:delta situation)]
    (try
      (let [new-hospital (function hospital param1 param2)]
        {:hospital new-hospital
         :delta (+ current-delta param3)})
      (catch IllegalStateException e
        situation)
      (catch AssertionError e
        situation))))

; creating test that validates the property of the system
; no matter the actions failed or succeeded
; invalid parameters are passed to the functions
(defspec simulates-one-day-at-hospital-dont-lose-people 50
  (prop/for-all
    [initial-hospital hospital-gen]
    (let [actions (gen/generate (actions-gen initial-hospital))
          initial-situation {:hospital initial-hospital :delta 0}
          initial-total-patients (total-patients initial-hospital)
          final-situation (reduce executes-an-action initial-situation actions)
          final-total-patients (total-patients (:hospital final-situation))
          final-delta (:delta final-situation)]
      (println initial-total-patients final-total-patients final-delta)
      (is (= (- final-total-patients final-delta) initial-total-patients)))))
;---------------------

; tests based on examples thought by programmer
(deftest fits-in-line?-test
  ; boundary zero
  (testing "It fits in line when line is empty"
    (is (fits-in-line? {:wait h.model/empty-line} :wait)))
  ; boundary limit
  (testing "It does not fit in line when line is full"
    (is (not (fits-in-line? {:wait (conj h.model/empty-line "1" "2" "3" "4" "5")} :wait))))
  ; boundary above limit
  (testing "It does not fit in line when line is more than full"
    (is (not (fits-in-line? {:wait (conj h.model/empty-line "1" "2" "3" "4" "5")} :wait))))
  ; inside boundary
  (testing "It fits in line when line is not full"
    (is (fits-in-line? {:wait (conj h.model/empty-line "1" "2" "3" "4")} :wait))
    (is (fits-in-line? {:wait (conj h.model/empty-line "1" "2")} :wait)))
  ; boundary department
  (testing "It does not fit in line when department does not exist"
    (is (not (fits-in-line? {:wait h.model/empty-line} :w)))))