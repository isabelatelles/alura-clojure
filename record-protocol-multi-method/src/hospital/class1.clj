(ns hospital.class1
  (:use clojure.pprint))

(defrecord Patient [id name date-of-birth])

(pprint (->Patient 15 "isabela" "05/08/2000"))
(pprint (Patient. 15 "isabela" "05/08/2000"))
(pprint (Patient. "isabela" 15 "05/08/2000"))
(pprint (map->Patient {:id 15 :name "isabela" :date-of-birth "05/08/2000"}))
(pprint (map->Patient {:id 15 :name "isabela"}))
(pprint (map->Patient {:id 15 :name "isabela" :date-of-birth "05/08/2000" :rg "2222"}))
;(pprint (Patient. "isabela" "05/08/2000"))
(pprint (= (->Patient 15 "isabela" "05/08/2000") (->Patient 153 "isabela" "05/08/2000")))
(println (.name (->Patient 15 "isabela" "05/08/2000")))
(println (:id (->Patient 15 "isabela" "05/08/2000")))
(println (record? (->Patient 15 "isabela" "05/08/2000")))
(println (class (->Patient 15 "isabela" "05/08/2000")))
(println (vals (->Patient 15 "isabela" "05/08/2000")))