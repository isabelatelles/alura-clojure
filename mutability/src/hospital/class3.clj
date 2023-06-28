(ns hospital.class3
  (:use clojure.pprint)
  (:require [hospital.model :as h.model]
            [hospital.logic :as h.logic]))

(defn experiment-atom
  []
  (let [example-hospital (atom {:wait h.model/empty-queue})] ; atom is a wrapper of a value
    (pprint example-hospital)
    (pprint (deref example-hospital))
    (pprint @example-hospital)
    ; (assoc @example-hospital :lab1 h.model/empty-queue) won't have effect
    (swap! example-hospital assoc :lab1 h.model/empty-queue)
    (swap! example-hospital assoc :lab2 h.model/empty-queue)
    (pprint example-hospital)

    ; (update @example-hospital :lab1 conj "111") won't have effect
    (swap! example-hospital update :lab1 conj "111")
    (pprint example-hospital)))

;(experiment-atom)

(defn bad-arrives-at!
  [hospital person]
  (swap! hospital h.logic/arrives-at-paused-logging :wait person)
  (println "after insert" person))

(defn simulates-one-day-in-parallel []
  (let [hospital (atom (h.model/new-hospital))]
    (.start (Thread. (fn [] (bad-arrives-at! hospital "111"))))
    (.start (Thread. (fn [] (bad-arrives-at! hospital "222"))))
    (.start (Thread. (fn [] (bad-arrives-at! hospital "333"))))
    (.start (Thread. (fn [] (bad-arrives-at! hospital "444"))))
    (.start (Thread. (fn [] (bad-arrives-at! hospital "555"))))
    (.start (Thread. (fn [] (bad-arrives-at! hospital "666"))))
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

(simulates-one-day-in-parallel)

; Options to deal with concurrency:
; Lock issues: threads idle, when synchronize and when not to, synchronize the whole function or just a piece of the code
; Busy retry: swap tries to modify atom but when it realizes it has already changed by another
; operation in parallel, it will retry the modification. it is important that the code swap
; will implement to be pure since if we know there will be retries, side effects can not be good