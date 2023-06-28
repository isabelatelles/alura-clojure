(ns owasp.class9)

;insuficient logging and monitoring

(def db {:matheus.bernardes "banana" :guilherme.silveira "senha"})

(def login-limit 30)

(def username-attempts (atom {}))

(defn my-inc
  [x]
  (if x
    (inc x)
    1))

(defn attempt-login?
  [username]
  (swap! username-attempts update-in [username] my-inc)
  (<= (get @username-attempts username) login-limit))

(defn login
  [username password]
  (let [keyword-username (keyword username)]
    (if (attempt-login? keyword-username)
      (let [found-password (get db keyword-username)]
        (if (= found-password password)
          (do (swap! username-attempts update-in [keyword-username] * 0)
              true)
          false))
      (throw (Exception. "Ha too many attempts")))))

(println (dotimes [_ 29] (login "matheus.bernardes" "32938")))
(println @username-attempts)
(println (login "matheus.bernardes" "banana"))
(println (login "matheus.bernardes" "323232"))
(println @username-attempts)