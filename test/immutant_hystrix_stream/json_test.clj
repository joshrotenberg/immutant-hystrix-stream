(ns immutant-hystrix-stream.json-test
  (:require [com.netflix.hystrix.core :refer  [defcommand]])
  (:require [immutant-hystrix-stream.json :refer :all]
            [clojure.test :refer :all])
  (:import [com.netflix.hystrix HystrixCommandMetrics]))

(defcommand my-command
  [n]
  (+ 20 n))

(deftest a-test
  (testing "command to json"
    (println (my-command 20))
    (println (HystrixCommandMetrics/getInstances))
    (println (cmd->json (first (HystrixCommandMetrics/getInstances))))))
