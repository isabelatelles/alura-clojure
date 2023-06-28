(ns owasp.class7
  (:require [crypto.password.bcrypt :as password]))

; injection (prev cross site scripting xss)

(def database (atom {}))

(defn add
  [username document]
  (swap! database assoc-in [username] document))

(defn read-file
  [filename]
  (-> filename
      slurp
      clojure.string/split-lines))

(def common-passwords (read-file "src/common-passwords.txt"))

(defn is-common?
  [password]
  (some #(= password %) common-passwords))

(defn register-new-user!
  [username password name]
  (if (is-common? password)
    (throw (Exception. "Password too simple."))
    (let [encrypted (password/encrypt password)]
      ;(println (password/check "senha" encrypted))
      (add username {:username username :password encrypted :name name}))))

(def public-profile-template "<html>
                              <head><title>Welcome</title></head>
                              <body>
                              <h1>{{NAME}}</h1>
                              {{USERNAME}}
                              </body>
                              </html>")
(defn load-user
  [username]
  (get @database username))

(defn replace-symbol
  [content [symbol-name symbol-value]]
  (let [key (str "{{" symbol-name "}}")]
    (clojure.string/replace content key symbol-value)))

(defn render-template
  [content symbols]
  (reduce replace-symbol content symbols))

(defn view-public-profile
  [username]
  (let [user (load-user username)]
    (println user username)
    (render-template public-profile-template
                     {"USERNAME" (:username user)
                      "NAME" (:name user)})))

(println (register-new-user! "matheus.bernardes" "banana" "Matheus Bernardes"))
(println @database)
(println (view-public-profile "matheus.bernardes"))

(println (register-new-user! "guilherme.silveira" "senha02139" "<script>alert('oi');</script>Guilherme Silveira")) ; malicious code
;sanitize client input always!! through library
(println (view-public-profile "guilherme.silveira"))
