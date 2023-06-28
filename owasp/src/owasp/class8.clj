(ns owasp.class8)

; software and data integrity failures

(defn treat-dot-commas
  [word]
  (-> word
      (clojure.string/replace "." "")
      (clojure.string/replace "," ".")
      clojure.edn/read-string))

(println (treat-dot-commas "1111.1111,30"))
(println (treat-dot-commas "#(+ 2 2)"))