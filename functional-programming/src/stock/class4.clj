(ns stock.class4)

(def prices [30 700 1000])

(defn apply-discount?
  [brute-value]
  (> brute-value 100))

(defn discounted-value
  "Returns 10% discount if applicable"
  [brute-value]
  (if (apply-discount? brute-value)
    (let [discount-tax (/ 10 100)
          discount (* brute-value discount-tax)]
      (- brute-value discount))
    brute-value))

(println (map discounted-value prices))
(println (filter apply-discount? prices))
(println (reduce * 1 prices))