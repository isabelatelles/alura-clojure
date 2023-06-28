(ns owasp.class6)

; security misconfiguration
; least minimal privilege (example is AWS acess to resources like dynamodb, s3)

(defn continue
  [chain path parameters]
  (if chain
    (let [next-one (first chain)]
      (next-one (rest chain) path parameters))))

(defn headers-layer
  [chain path parameters]
  (let [result (continue chain path parameters)]
    (assoc-in result [:headers :x-framework] "Our Framework 3.1"))) ; vulnerability

(defn error-control-layer
  [chain path parameters]
  (try (continue chain path parameters)
       (catch Exception e {:error 500
                           :body (str (.getMessage e))})))

(defn log-layer
  [chain path parameters]
  ;logging sensitive data!!!!
  (println "logging" path)
  (continue chain path parameters))

(defn execution-layer
  [chain path parameters]
  (if (= path "/upload")
    (throw (Exception. "Upload failed")))
  (println "Execution for path" path)
  {:code 200, :body (str "result for " path)})

(defn service
  [path parameters]
  (let [chain [error-control-layer log-layer headers-layer execution-layer]]
    (continue chain path parameters)))

(println (service "/login" {:password "hello"}))
(println (service "/upload" {:upload-file "hi.txt"}))