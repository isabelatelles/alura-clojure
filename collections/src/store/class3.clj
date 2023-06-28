(ns store.class3
  (:require [store.db :as s.db]))

(defn get-total-orders-per-user
  [[user orders]]
  { :user user
    :total-orders (count orders) })

(defn get-total-orders-by-user
  []
  (->> (s.db/all-orders)
       (group-by :user)
       (map get-total-orders-per-user)
       println))

(get-total-orders-by-user)

(defn get-total-price-per-item
  [[_ item]]
  (* (get item :quantity 0) (get item :unit-price 0)))

(defn get-total-price-per-order
  [items-of-order]
  (->> items-of-order
       (map get-total-price-per-item)
       (reduce +)))

(defn get-total-price
  [orders]
  (->> orders
       (map :items)
       (map get-total-price-per-order)
       (reduce +)))

(defn get-total-orders-and-total-price-per-user
  [[user orders]]
  { :user user
    :total-orders (count orders)
    :total-price (get-total-price orders) })

(defn get-total-orders-and-total-price-by-user
  []
  (->> (s.db/all-orders)
       (group-by :user)
       (map get-total-orders-and-total-price-per-user)
       println))

(get-total-orders-and-total-price-by-user)