(ns hospital.model)

(def empty-queue clojure.lang.PersistentQueue/EMPTY)

(defn new-hospital []
  {:wait empty-queue
   :lab1 empty-queue
   :lab2 empty-queue})