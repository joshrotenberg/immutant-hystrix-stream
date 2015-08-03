(ns immutant-hystrix-stream.metrics-test
  (:require [immutant-hystrix-stream.metrics :refer :all]
            [immutant-hystrix-stream.test.commands :refer :all]
            [clojure.test :refer :all])
  (:import [com.netflix.hystrix.strategy.concurrency HystrixRequestContext]))

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
  (testing "commands"
    ;; run the commands so we know they've been registered
    (let [fn-cmd-res (my-fn-command 20 30)
          fn-overload-cmd-res (my-overload-fn-command 20 30 40)
          to-upper-collapser-res (to-upper-collapser "abc")
          cmds (command-metrics)
          threadpools (threadpool-metrics)
          collapsers (collapser-metrics)]
      (is (= 3 (count cmds)))
      (is (= 1 (count threadpools)))
      (is (= 1 (count collapsers))))))
