(ns store.class1)

(defn my-map
  [function sequence]
  (let [first-element (first sequence)]
    (if (not (nil? first-element))
      (do
        (function first-element)
        ; optmization by transfroming recursion to loop
        ; so stack will not overflow
        ; tail recursion
        (recur function (rest sequence))))))

(my-map println ["d" false "i"])
(my-map println (range 5000))