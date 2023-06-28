(ns hospital.class1
  (:use clojure.pprint)
  (:require [schema.core :as s]
            [clojure.spec.alpha :as spec]))

(pprint (s/validate Long 15))
;(pprint (s/validate Long "15")) ;error
(pprint (spec/valid? long 15))

;enables validation at runtime execution
(s/set-fn-validation! true)

(s/defn simple-test
  [x :- Long]
  (println x))
(simple-test 30)
;(simple-test "20") ;error

; https://clojure.org/guides/spec#_using_spec_for_validation

(spec/check-asserts true)
(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(spec/def :acct/email-type (spec/and string? #(re-matches email-regex %)))
(spec/def :acct/first-name string?)
(spec/def :acct/last-name string?)
(spec/def :acct/email :acct/email-type)
(spec/def :acct/person (spec/keys :req [:acct/first-name :acct/last-name :acct/email]
                                  :opt [:acct/phone]))

(defn person-name
  [person]
  (let [p (spec/assert :acct/person person)]
    (str (:acct/first-name p) " " (:acct/last-name p))))

(person-name {:acct/first-name "Bugs"
              :acct/last-name "Bunny"
              :acct/email "bugs@example.com"})
(person-name 42)