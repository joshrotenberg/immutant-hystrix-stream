(ns immutant-hystrix-stream.json-test
  (:require [cheshire.core :refer :all]
            [immutant-hystrix-stream.test.commands :refer :all])
  (:require [immutant-hystrix-stream.json :refer :all]
            [clojure.test :refer :all])
  (:import [com.netflix.hystrix
            HystrixCommandMetrics
            HystrixThreadPoolMetrics
            HystrixCollapserMetrics]
           [com.netflix.hystrix.strategy.concurrency HystrixRequestContext]))

(defn request-context-fixture
  [f]
  (try
    (HystrixRequestContext/initializeContext)
    (f)
    (finally
      (when-let [c (HystrixRequestContext/getContextForCurrentThread)]
        (.shutdown c)))))

(use-fixtures :each request-context-fixture)

(deftest a-test
  (testing "command to json"
    ;; run the commands so we know they've been registered
    (let [fn-cmd-res (my-fn-command 20 30)
          fn-overload-cmd-res (my-overload-fn-command 20 30 40)
          to-upper-collapser-res (to-upper-collapser "abc")
          encoded-cmds (command-metrics)
          encoded-threadpools (threadpool-metrics)
          encoded-collapsers (collapser-metrics)]
      (is (= 3 (count encoded-cmds)))
      (is (= 1 (count encoded-threadpools)))
      (is (= 1 (count encoded-collapsers))))))
