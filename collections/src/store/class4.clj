(ns store.class4
  (:require [store.db :as s.db]
            [store.logic :as s.logic]))

(println (s.logic/get-summary-of-orders (s.db/all-orders)))

(let [orders (s.db/all-orders)
      summary (s.logic/get-summary-of-orders orders)]
  (println "ordered by total price" (sort-by :total-price summary))
  (println "ordered by reverse total price" (reverse (sort-by :total-price summary)))
  (println "ordered by id" (sort-by :user summary))
  (println "quantity of bags in first order" (get-in orders [0 :items :bag :quantity]))
  (println (get summary 1))
  (println (class summary))
  (println (nth summary 1))
  (println (take 2 summary)))