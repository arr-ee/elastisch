(ns clojurewerkz.elastisch.rest-api.queries.fuzzy-query-test
  (:require [clojurewerkz.elastisch.rest.document      :as doc]
            [clojurewerkz.elastisch.rest.index         :as idx]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.fixtures :as fx])
  (:use clojure.test clojurewerkz.elastisch.rest.response))


(use-fixtures :each fx/reset-indexes fx/prepopulate-articles-index)

;;
;; flt query
;;

(deftest ^{:query true} test-basic-fuzzy-query-with-string-fields
  (let [index-name   "articles"
        mapping-type "article"
        response     (doc/search index-name mapping-type :query (q/fuzzy :title "Nueva"))
        hits         (hits-from response)]
    (is (any-hits? response))
    (is (= 1 (total-hits response)))
    (is (= #{"3"} (ids-from response)))))

(deftest ^{:query true} test-basic-fuzzy-query-with-numeric-fields
  (let [index-name   "articles"
        mapping-type "article"
        response (doc/search index-name mapping-type :query (q/fuzzy :number-of-edits {:value 13000 :min_similarity 3}))
        hits     (hits-from response)]
    (is (any-hits? response))
    (is (= 1 (total-hits response)))
    (is (= #{"4"} (ids-from response)))))

;; TODO: ES 0.19.2 spills 500s complaining about java.lang.IllegalArgumentException: minimumSimilarity >= 1
#_ (deftest ^{:query true} test-basic-fuzzy-query-with-date-fields
     (let [index-name   "articles"
           mapping-type "article"
           response     (doc/search index-name mapping-type :query (q/fuzzy "last-edit.date" {:value "2012-03-25T12:00:00" :min_similarity "1d"}))
           hits         (hits-from response)]
       (is (any-hits? response))
       (is (= 1 (total-hits response)))
       (is (= #{"4"} (ids-from response)))))
