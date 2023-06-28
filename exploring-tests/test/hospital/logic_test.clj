(ns hospital.logic-test
  (:require [clojure.test :refer :all]
            [hospital.logic :refer :all]
            [schema.core :as s]
            [hospital.model :as h.model]))

(s/set-fn-validation! true)

(deftest fits-in-line?-test
  ; boundary zero
  (testing "It fits in line when line is empty"
    (is (fits-in-line? {:wait []} :wait)))
  ; boundary limit
  (testing "It does not fit in line when line is full"
    (is (not (fits-in-line? {:wait [1 2 3 4 5]} :wait))))
  ; boundary above limit
  (testing "It does not fit in line when line is more than full"
    (is (not (fits-in-line? {:wait [1 2 3 4 5 6]} :wait))))
  ; inside boundary
  (testing "It fits in line when line is not full"
    (is (fits-in-line? {:wait [1 2 3 4]} :wait))
    (is (fits-in-line? {:wait [1 2]} :wait)))
  ; boundary department
  (testing "It does not fit in line when department does not exist"
    (is (not (fits-in-line? {:wait []} :w)))))

(deftest arrives-at-test
  (testing "accepts people while it fits in line"
    (is (= {:wait [1, 2, 3, 4, 5]}
           (arrives-at {:wait [1, 2, 3, 4]}, :wait, 5)))
    (is (= {:wait [4, 2, 3]}
           (arrives-at {:wait [4, 2]}, :wait, 3))))
  (testing "it does not accept when it does not fit in line"
    (is (try
          (arrives-at {:wait [10, 55, 3, 7, 9]}, :wait, 5)
          false
          (catch clojure.lang.ExceptionInfo e
            (= :impossible-to-fit-in-line (:type (ex-data e))))))))

(deftest transfer-test
  (testing "accepts people when it fits in line"
    (let [original-hospital {:wait-from (conj h.model/empty-line "5"), :wait-to h.model/empty-line}]
      (is (= {:wait-from [], :wait-to ["5"]}
             (transfer original-hospital, :wait-from, :wait-to))))
    (let [original-hospital {:wait-from (conj h.model/empty-line "51" "5"), :wait-to (conj h.model/empty-line "13")}]
      (is (= {:wait-from ["5"], :wait-to ["13", "51"]}
             (transfer original-hospital, :wait-from, :wait-to)))))
  (testing "it does not accept when it does not fit in line"
    (let [full-hospital {:wait-from (conj h.model/empty-line "5"), :wait-to (conj h.model/empty-line "10" "55" "3" "7" "9")}]
      (is (thrown? clojure.lang.ExceptionInfo
                   (transfer full-hospital :wait-from :wait-to)))))
  (testing "it does not accept when department does not exist"
    (is (thrown? AssertionError
                 (transfer {:wait-from (conj h.model/empty-line "5"), :wait-to h.model/empty-line } :wait :wait-to)))
    (is (thrown? AssertionError
                 (transfer {:wait-from (conj h.model/empty-line "5"), :wait-to h.model/empty-line } :wait-from :x-ray)))))

(deftest arrives-at-public-test
  (let [full-hospital {:wait [10, 55, 3, 7, 9]}]
    (testing "accepts people while it fits in line"
      (is (= {:hospital full-hospital, :result :success}
             (arrives-at-public {:wait [10, 55, 3, 7]}, :wait, 9)))
      (is (= {:hospital {:wait [4, 2, 3]}, :result :success}
             (arrives-at-public {:wait [4, 2]}, :wait, 3))))
    (testing "it does not accept when it does not fit in line"
      (is (= {:hospital full-hospital, :result :impossible-to-fit-in-line}
             (arrives-at-public full-hospital, :wait, 5))))))