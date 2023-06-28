(ns hospital.class6
  (:use clojure.pprint)
  (:require [hospital.model :as h.model]))

; So far we worked only with big atoms. It can be interesting to
; split the atom's content in case there are a lot of conflicts
; with the content executed inside pure functions

; O uso de átomo é mais simples mas refs facilitam a coordenação de trabalho entre dois valores mutáveis, trazendo todos os problemas de coordenação junto.

(defn fits-in-line?
  [line]
  (-> line
      count
      (< 5)))

(defn arrives-at
  [line person]
  (if (fits-in-line? line)
    (conj line person)
    (throw (ex-info "Line is full." {:person person}))))

(defn arrives-at-with-ref-set!
  [hospital person]
  (let [line (get hospital :wait)]
    (ref-set line (arrives-at @line person))))

(defn arrives-at!
  [hospital person]
  (let [line (get hospital :wait)]
    (alter line arrives-at person)))

(defn simulates-a-day []
  (let [hospital {:wait (ref h.model/empty-queue)
                  :lab1 (ref h.model/empty-queue)
                  :lab2 (ref h.model/empty-queue)
                  :lab3 (ref h.model/empty-queue)}]
    (dosync
      (arrives-at! hospital "guilherme")
      (arrives-at! hospital "maria")
      (arrives-at! hospital "joana")
      (arrives-at! hospital "isabela")
      (arrives-at! hospital "daniela")
      ;(arrives-at! hospital "lucia")
      )
    (pprint hospital)))

;(simulates-a-day)

(defn async-arrivest-at!
  [hospital person]
  (future
    (Thread/sleep (rand 5000))
    (dosync
      (println "Trying synchronized code" person)
      (arrives-at! hospital person))))

(defn simulates-a-day-async []
  (let [hospital {:wait (ref h.model/empty-queue)
                  :lab1 (ref h.model/empty-queue)
                  :lab2 (ref h.model/empty-queue)
                  :lab3 (ref h.model/empty-queue)}
        futures (mapv #(async-arrivest-at! hospital %) (range 10))]
    ; global symbol of futures we will be able to see exceptions
    (future
      (dotimes [n 4]
        (Thread/sleep 2000)
        (pprint hospital)
        (pprint futures)))))

(simulates-a-day-async)