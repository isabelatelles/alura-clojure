(ns stock.class5)

(def stock {:bag 10 :shirt 5})

(println "Keys" (keys stock))
(println "Vals" (vals stock))
(println (assoc stock :desk 3))
(println (update stock :bag inc))

(def order {:bag {:quantity 10 :price 90}
            :shirt {:quantity 3 :price 50}})
(println (get order :shirt))
(println (get order :desk {}))
(println (order :shirt))

(println (update-in order [:bag :quantity] inc))
(println (-> order
             :bag
             :quantity))

(defn legacy-product-price
  [[_ {:keys [quantity price]}]]
  (* quantity price))

(println (map legacy-product-price order))

(defn legacy-order-price
  [order]
  (->> order
      (map legacy-product-price)
      (reduce +)))

(println (legacy-order-price order))

(defn product-price
  [{:keys [quantity price]}]
  (* quantity price))

(defn order-price
  [order]
  (->> order
       vals
       (map product-price)
       (reduce +)))

(println (order-price order))

(def order-with-no-price {:bag {:quantity 10 :price 90}
                          :shirt {:quantity 3 :price 50}
                          :desk {:quantity 9}})

(defn no-price?
  [product]
  (<= (get product :price 0) 0))

(defn filter-no-price
  [order]
  (->> order
       vals
       (filter no-price?)))
(println (filter-no-price order-with-no-price))