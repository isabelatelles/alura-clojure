(ns owasp.class5)

;broken access control

(defn bad-get-lyrics
  [lyrics-name]
  (->> lyrics-name
       (str "resources/")
       slurp))

;(println (bad-get-lyrics "shake-it-off"))
;(println (bad-get-lyrics "../src/owasp/core.clj"))          ; external user has access to an internal file that shouldnt!!

(def lyrics { :shake-it-off "resources/shake-it-off" })

(defn get-lyrics
  [lyrics-name]
  (->> lyrics-name
       keyword
       (get lyrics)
       slurp))

;(println (get-lyrics "shake-it-off"))
;(println (get-lyrics "../src/owasp/core.clj")) ; we prevented external user to acess private information




(def permissions {"1234-ABDC" (set [394320])
                  "1212-2DAD" (set [123899, 123])})

(defn user-can-read-news?
  [user-id news-id]
  (-> permissions
      (get user-id)
      (get news-id)))

(defn slug-to-id
  [news-slug]
  (let [db {"/news/owasp-new-report-is-out" 394320
            "/news/owasp-old-report-is-updated" 123
            "/news/old" 123899}]
    (get db news-slug)))

(defn update-news!
  [news-id]
  (str "updated " news-id))

(defn bad-edit-news
  [news-slug query-params session-params]
  (if-let [user-id (:user-id session-params)]
    (if-let [news-id (slug-to-id news-slug)]
      (if (user-can-read-news? user-id news-id)
        (update-news! (:news-id query-params))
        (println "can not read this news" news-id)))))

(println (bad-edit-news "/news/owasp-new-report-is-out" {:news-id 394320} {:user-id "1234-ABDC"}))
(println (bad-edit-news "/news/owasp-new-report-is-out" {:news-id 123} {:user-id "1234-ABDC"}))

(defn edit-news
  [news-slug query-params session-params]
  (if-let [user-id (:user-id session-params)]
    (if-let [news-id (:news-id query-params)]
      (if (user-can-read-news? user-id news-id)
        (update-news! news-id)
        (println "can not read this news" news-id)))))

(println (edit-news "/news/owasp-new-report-is-out" {:news-id 394320} {:user-id "1234-ABDC"}))
(println (edit-news "/news/owasp-new-report-is-out" {:news-id 123} {:user-id "1234-ABDC"}))
