(ns owasp.class2
  (:require [crypto.password.bcrypt :as password]))

; identification and authentication failures
; solutions for this problem: encrypt password always,
; and prevent common passwords to be created
(def database (atom {}))

(defn add
  [table document]
  (swap! database update-in [table] conj document))

(defn register-new-user!
  [username password]
  (if (is-common? password)
    (throw (Exception. "Password too simple."))
    (let [encrypted (password/encrypt password)]
      ;(println (password/check "senha" encrypted))
      (add :users {:username username :password encrypted}))))

(defn read-file
  [filename]
  (-> filename
      slurp
      clojure.string/split-lines))

(def common-passwords (read-file "src/common-passwords.txt"))

(defn is-common?
  [password]
  (some #(= password %) common-passwords))

(println (is-common? "senha"))
(println (is-common? "banana"))

;(println (register-new-user! "guilherme.bernardes" "senha"))
(println (register-new-user! "matheus.bernardes" "banana"))