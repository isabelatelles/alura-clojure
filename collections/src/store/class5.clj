(ns store.class5
  (:require [store.logic :as s.logic]
            [store.db :as s.db]))

(defn spent-a-lot?
  [user-info]
  (> (:total-price user-info) 500))

(let [orders (s.db/all-orders)
      summary (s.logic/get-summary-of-orders orders)]
  (println "keep" (keep spent-a-lot? summary))              ; map + filter
  (println "filter " (filter spent-a-lot? summary))
  (println (take 2 (range 1000000))))                       ; lazy, not eager, it is not generating all numbers of range, only 2

; map is lazy + eager, it can break the sequence in chunks and process it chunk at a time