(ns hospital.core
  (:use clojure.pprint)
  (:require [clojure.test.check.generators :as gen]
            [clojure.spec.gen.alpha :as gen2]
            [clojure.spec.alpha :as s]
            [schema-generators.generators :as g]
            [hospital.model :as h.model]))

(println (gen/sample gen/boolean 3))
(println (gen/sample gen/string-alphanumeric 5))
(println (gen2/sample (s/gen boolean?) 2))
(println (gen2/generate (s/gen boolean?)))
(println (gen2/generate (s/gen string?)))

(println (gen/sample (gen/vector gen/small-integer 3) 10))
(println (gen/sample (gen/vector gen/small-integer 1 5) 10))

(pprint (g/sample 2 h.model/Hospital))
(pprint (g/generate h.model/Hospital))