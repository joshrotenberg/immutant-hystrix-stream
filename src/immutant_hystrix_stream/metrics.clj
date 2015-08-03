(ns immutant-hystrix-stream.metrics
  (:import [com.netflix.hystrix
            HystrixCommandMetrics
            HystrixThreadPoolMetrics
            HystrixCollapserMetrics]))

(defn command-metrics
  "Retrive command metrics sequence"
  []
  (seq (HystrixCommandMetrics/getInstances)))

(defn threadpool-metrics
  "Retrieve threadpool metrics sequence (for threadpools with completed tasks)"
  []
  (filter #(> (-> %
                  (.getCurrentCompletedTaskCount)
                  (.intValue))
              0) (seq (HystrixThreadPoolMetrics/getInstances))))

(defn collapser-metrics
  "Retrieve collapser metric sequence"
  []
  (seq (HystrixCollapserMetrics/getInstances)))

(defn all-metrics
  "Returns a lazy sequence of all currently registered metrics"
  []
  (concat (command-metrics) (threadpool-metrics) (collapser-metrics)))
