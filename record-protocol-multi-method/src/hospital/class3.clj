(ns hospital.class3
  (:use clojure.pprint)
  (:require [hospital.logic :as h.logic]))

(defn load-patient
  [id]
  (println "Carregando" id)
  (Thread/sleep 1000)
  { :id id, :loading-in (h.logic/now)})

(defn load-if-does-not-exist
  [cache id loader]
  (if (contains? cache id)
    cache
    (let [patient (loader id)]
      (assoc cache id patient))))

(defprotocol Loadable
  (load! [this id]))

(defrecord Cache
  [cache loader]                                            ; cache is an atom
  Loadable
  (load!
    [this id]
    (swap! cache load-if-does-not-exist id loader)
    (get @cache id)))

(def patients (->Cache (atom {}) load-patient))

(pprint patients)
(load! patients 15)
(load! patients 30)
(load! patients 15)
(pprint patients)