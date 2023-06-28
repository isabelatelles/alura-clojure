(ns store.logic)

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

(defn get-summary-of-orders
  [orders]
  (->> orders
       (group-by :user)
       (map get-total-orders-and-total-price-per-user)))