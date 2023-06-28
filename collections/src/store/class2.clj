(ns store.class2)

(defn my-count-reduce
  ([elements]
   (my-count-reduce 0 elements))
  ([current-total elements]
   (if (seq elements)
     (recur (inc current-total) (next elements))
     current-total)))

(println (my-count-reduce 0 ["a" "b"]))
(println (my-count-reduce ["a" "b" "c"]))
