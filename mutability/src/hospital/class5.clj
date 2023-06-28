(ns hospital.class5
  (:use clojure.pprint)
  (:require [hospital.model :as h.model]
            [hospital.logic :as h.logic]))

(defn arrives-at!
  [hospital person]
  (swap! hospital h.logic/arrives-at :wait person))

(defn transfer!
  [hospital from to]
  (swap! hospital h.logic/transfer from to))

(defn simulates-a-day []
  (let [hospital (atom (h.model/new-hospital))]
    (arrives-at! hospital "joao")
    (arrives-at! hospital "maria")
    (arrives-at! hospital "guilherme")
    (arrives-at! hospital "daniela")
    (transfer! hospital :wait :lab1)
    (transfer! hospital :wait :lab2)
    (transfer! hospital :wait :lab2)
    (transfer! hospital :lab2 :lab3)
    (pprint hospital)))

(simulates-a-day)