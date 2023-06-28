(ns hospital.class4
  (:use clojure.pprint)
  (:require [hospital.model :as h.model]
            [hospital.logic :as h.logic]))

(defn bad-arrives-at!
  [hospital person]
  (swap! hospital h.logic/arrives-at-paused-logging :wait person)
  (println "after insert" person))

(defn starts-thread-arrives-at
  ([hospital]
   (fn [person] (starts-thread-arrives-at hospital person)))
  ([hospital person]
   (.start (Thread. (fn [] (bad-arrives-at! hospital person))))))

(defn simulates-one-day-in-parallel-without-partial []
  (let [hospital (atom (h.model/new-hospital))
        people ["111" "222" "333" "444" "555" "666"]
        starts (starts-thread-arrives-at hospital)]
    (mapv starts people) ; map is lazy, we use mapv to force the map to actually happen even though we dont use the vector generated
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simulates-one-day-in-parallel-without-partial)

(defn starts-thread-arrives-at-partial
  [hospital person]
  (.start (Thread. (fn [] (bad-arrives-at! hospital person)))))

(defn simulates-one-day-in-parallel-with-partial []
  (let [hospital (atom (h.model/new-hospital))
        people ["111" "222" "333" "444" "555" "666"]
        starts (partial starts-thread-arrives-at-partial hospital)]
    (mapv starts people) ; map is lazy, we use mapv to force the map to actually happen even though we dont use the vector generated
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simulates-one-day-in-parallel-with-partial)

(defn simulates-one-day-in-parallel-with-doseq []
  (let [hospital (atom (h.model/new-hospital))
        people ["111" "222" "333" "444" "555" "666"]]

    (doseq [person people]
      (starts-thread-arrives-at hospital person))
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

;(simulates-one-day-in-parallel-with-doseq)

(defn simulates-one-day-in-parallel-with-doseq []
  (let [hospital (atom (h.model/new-hospital))]

    (dotimes [person 6]
      (starts-thread-arrives-at hospital person))
    (.start (Thread. (fn [] (Thread/sleep 8000)
                       (pprint hospital))))))

(simulates-one-day-in-parallel-with-doseq)