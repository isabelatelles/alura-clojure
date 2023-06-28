(ns owasp.class3)

;cryptographic failures

(defn continue
  [chain path parameters]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) path parameters))))

(defn execution-layer
  [chain path parameters]
  (println "executing for path" path))

(defn do-upload
  [parameters]
  (println "uploading for params" parameters))

(defn upload-layer
  [chain path parameters]
  (if (:upload-file parameters)
    (do-upload parameters))
  (continue chain path parameters))

(defn log-layer
  [chain path parameters]
  ;logging sensitive data!!!!
  (println "logging" path parameters)
  (continue chain path parameters))

(defn service
  [path parameters]
  (let [chain [log-layer upload-layer execution-layer]]
    (continue chain path parameters)))

(service "/upload" {:upload-file "hi.txt"})
(service "/login" {:password "password"})