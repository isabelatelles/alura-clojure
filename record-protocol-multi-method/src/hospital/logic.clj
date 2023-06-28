(ns hospital.logic
  (:require [hospital.model :as h.model]))

(defn now
  []
  (h.model/to-ms (java.util.Date.)))