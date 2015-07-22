(ns immutant-hystrix-stream.json
  (:require [cheshire.core :refer :all]
            [cheshire.generate :refer [add-encoder encode-str remove-encoder]])
  (:import [com.netflix.hystrix HystrixCommandKey HystrixCircuitBreaker$Factory]
           [com.netflix.hystrix.util HystrixRollingNumberEvent]))

;; encoders
(add-encoder com.netflix.hystrix.HystrixCommandMetrics
             (fn [c jg]
               (let [key (.getCommandKey c)
                     group (.getCommandGroup c)
                     circuit-breaker (HystrixCircuitBreaker$Factory/getInstance key)
                     health-counts (.getHealthCounts c)
                     command-properties (.getProperties c)]
                 (doto jg
                     (.writeStartObject)
                     (.writeStringField "type" "HystrixCommand")
                     (.writeStringField "name" (.name key))
                     (.writeStringField "group" (.name group))
                     (.writeNumberField "currentTime" (System/currentTimeMillis)))

                 (if (= circuit-breaker nil)
                   (.writeBooleanField jg "isCircuitBreakerOpen" false)
                   (.writeBooleanField jg "isCircuitBreakerOpen" (.isOpen circuit-breaker)))

                 (doto jg                 
                   (.writeNumberField "errorPercentage" (.getErrorPercentage health-counts))
                   (.writeNumberField "errorCount" (.getErrorCount health-counts))
                   (.writeNumberField "requestCount" (.getTotalRequests health-counts))

                   (.writeNumberField "rollingCountBadRequests"
                                      (.getRollingCount c (HystrixRollingNumberEvent/BAD_REQUEST)))
                   (.writeNumberField "rollingCountCollapsedRequests"
                                      (.getRollingCount c (HystrixRollingNumberEvent/COLLAPSED)))
                   (.writeNumberField "rollingCountEmit"
                                      (.getRollingCount c (HystrixRollingNumberEvent/EMIT))) 
                   (.writeNumberField "rollingCountExceptionsThrown"
                                      (.getRollingCount c (HystrixRollingNumberEvent/EXCEPTION_THROWN)))
                   (.writeNumberField "rollingCountFailure"
                                      (.getRollingCount c (HystrixRollingNumberEvent/FAILURE)))
                   (.writeNumberField "rollingCountEmit"
                                      (.getRollingCount c (HystrixRollingNumberEvent/FALLBACK_EMIT)))
                   (.writeNumberField "rollingCountFallbackFailure"
                                      (.getRollingCount c (HystrixRollingNumberEvent/FALLBACK_FAILURE)))
                   (.writeNumberField "rollingCountFallbackRejection"
                                      (.getRollingCount c (HystrixRollingNumberEvent/FALLBACK_REJECTION)))
                   (.writeNumberField "rollingCountFallbackSuccess"
                                      (.getRollingCount c (HystrixRollingNumberEvent/FALLBACK_SUCCESS)))
                   (.writeNumberField "rollingCountResponsesFromCache"
                                      (.getRollingCount c (HystrixRollingNumberEvent/RESPONSE_FROM_CACHE)))
                   (.writeNumberField "rollingCountSemaphoreRejected"
                                      (.getRollingCount c (HystrixRollingNumberEvent/SEMAPHORE_REJECTED)))
                   (.writeNumberField "rollingCountShortCircuited"
                                      (.getRollingCount c (HystrixRollingNumberEvent/SHORT_CIRCUITED)))
                   (.writeNumberField "rollingCountSuccess"
                                      (.getRollingCount c (HystrixRollingNumberEvent/SUCCESS)))
                   (.writeNumberField "rollingCountThreadPoolRejected"
                                      (.getRollingCount c (HystrixRollingNumberEvent/THREAD_POOL_REJECTED)))
                   (.writeNumberField "rollingCountTimeout"
                                      (.getRollingCount c (HystrixRollingNumberEvent/TIMEOUT)))
                   (.writeNumberField "currentConcurrentExecutionCount" (.getCurrentConcurrentExecutionCount c)) 
                   (.writeNumberField "rollingMaxConcurrentExecutionCount" (.getRollingMaxConcurrentExecutions c))

                   (.writeNumberField "latencyExecute_mean" (.getExecutionTimeMean c))
                   (.writeObjectFieldStart "latencyExecute")
                   (.writeNumberField "0" (.getExecutionTimePercentile c 0))
                   (.writeNumberField "25" (.getExecutionTimePercentile c 25))
                   (.writeNumberField "50" (.getExecutionTimePercentile c 50))
                   (.writeNumberField "75" (.getExecutionTimePercentile c 75))
                   (.writeNumberField "90" (.getExecutionTimePercentile c 90))
                   (.writeNumberField "95" (.getExecutionTimePercentile c 95))
                   (.writeNumberField "99" (.getExecutionTimePercentile c 99))
                   (.writeNumberField "99.5" (.getExecutionTimePercentile c 99.5))
                   (.writeNumberField "100" (.getExecutionTimePercentile c 100))
                   (.writeEndObject)                   
                   (.writeNumberField "latencyTotal_mean" (.getTotalTimeMean c ))
                   (.writeObjectFieldStart "latencyTotal")

                   (.writeNumberField "0" (.getTotalTimePercentile c 0))
                   (.writeNumberField "25" (.getTotalTimePercentile c 25))
                   (.writeNumberField "50" (.getTotalTimePercentile c 50))
                   (.writeNumberField "75" (.getTotalTimePercentile c 75))
                   (.writeNumberField "90" (.getTotalTimePercentile c 90))
                   (.writeNumberField "95" (.getTotalTimePercentile c 95))
                   (.writeNumberField "99" (.getTotalTimePercentile c 99))
                   (.writeNumberField "99.5" (.getTotalTimePercentile c 99.5))
                   (.writeNumberField "100" (.getTotalTimePercentile c 100))
                   (.writeEndObject)

                   (.writeNumberField "propertyValue_circuitBreakerRequestVolumeThreshold"
                                      (-> command-properties
                                          (.circuitBreakerRequestVolumeThreshold)
                                          (.get)))
                   (.writeNumberField "propertyValue_circuitBreakerSleepWindowInMilliseconds"
                                      (-> command-properties
                                          (.circuitBreakerSleepWindowInMilliseconds)
                                          (.get)))
                   (.writeNumberField "propertyValue_circuitBreakerErrorThresholdPercentage"
                                      (-> command-properties
                                          (.circuitBreakerErrorThresholdPercentage)
                                          (.get)))
                   (.writeBooleanField "propertyValue_circuitBreakerForceOpen"
                                       (-> command-properties
                                           (.circuitBreakerForceOpen)
                                           (.get)))
                   (.writeBooleanField "propertyValue_circuitBreakerForceClosed"
                                       (-> command-properties
                                           (.circuitBreakerForceClosed)
                                           (.get)))
                   (.writeBooleanField "propertyValue_circuitBreakerEnabled"
                                       (-> command-properties
                                           (.circuitBreakerEnabled)
                                           (.get)))

                   (.writeStringField "propertyValue_executionIsolationStrategy"
                                      (-> command-properties
                                          (.executionIsolationStrategy)
                                          (.get)
                                          (.name)))
                   (.writeNumberField "propertyValue_executionIsolationThreadTimeoutInMilliseconds"
                                      (-> command-properties
                                          (.executionTimeoutInMilliseconds)
                                          (.get)))
                   (.writeNumberField "propertyValue_executionTimeoutInMilliseconds"
                                      (-> command-properties
                                          (.executionTimeoutInMilliseconds)
                                          (.get)))
                   (.writeBooleanField "propertyValue_executionIsolationThreadInterruptOnTimeout"
                                       (-> command-properties
                                           (.executionIsolationThreadInterruptOnTimeout)
                                           (.get)))
                   (.writeStringField "propertyValue_executionIsolationThreadPoolKeyOverride"
                                      (-> command-properties
                                          (.executionIsolationThreadPoolKeyOverride)
                                          (.get)))
                   (.writeNumberField "propertyValue_executionIsolationSemaphoreMaxConcurrentRequests"
                                      (-> command-properties
                                          (.executionIsolationSemaphoreMaxConcurrentRequests)
                                          (.get)))
                   (.writeNumberField "propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests"
                                      (-> command-properties
                                          (.fallbackIsolationSemaphoreMaxConcurrentRequests)
                                          (.get)))
                   (.writeNumberField "propertyValue_metricsRollingStatisticalWindowInMilliseconds"
                                      (-> command-properties
                                          (.metricsRollingStatisticalWindowInMilliseconds)
                                          (.get)))

                   (.writeBooleanField "propertyValue_requestCacheEnabled"
                                       (-> command-properties
                                           (.requestCacheEnabled)
                                           (.get)))
                   (.writeBooleanField "propertyValue_requestLogEnabled"
                                       (-> command-properties
                                           (.requestLogEnabled)
                                           (.get)))
                                      
                   (.writeNumberField "reportingHosts" 1)
                   (.writeStringField "threadPool" (-> c (.getThreadPoolKey) (.name)))
                   
                   (.writeEndObject)))))

(add-encoder com.netflix.hystrix.HystrixThreadPoolMetrics
             (fn [t jg]
               (let [key (.getThreadPoolKey t)]
                 (doto jg
                   (.writeStartObject)
                   (.writeStringField "type" "HystrixThreadPool")
                   (.writeStringField "name" (.name key))
                   (.writeNumberField "currentTime" (System/currentTimeMillis))
                   (.writeNumberField "currentActiveCount" (-> t
                                                               (.getCurrentActiveCount)
                                                               (.intValue)))
                   (.writeNumberField "currentCompletedTaskCount"  (-> t
                                                                       (.getCurrentCompletedTaskCount)
                                                                       (.longValue) ))
                   (.writeNumberField "currentCorePoolSize"  (-> t
                                                                 (.getCurrentCorePoolSize)
                                                                 (.intValue)))
                   (.writeNumberField "currentLargestPoolSize" (-> t (.getCurrentLargestPoolSize) (.intValue)))
                   (.writeNumberField "currentMaximumPoolSize" (-> t (.getCurrentMaximumPoolSize) (.intValue)))
                   (.writeNumberField "currentPoolSize" (-> t (.getCurrentPoolSize) (.intValue)))
                   (.writeNumberField "currentQueueSize" (-> t (.getCurrentQueueSize) (.intValue)))
                   (.writeNumberField "currentTaskCount" (-> t (.getCurrentTaskCount) (.longValue)))
                   (.writeNumberField "rollingCountThreadsExecuted"
                                      (.getRollingCount t (HystrixRollingNumberEvent/THREAD_EXECUTION)))
                   (.writeNumberField "rollingMaxActiveThreads" (.getRollingMaxActiveThreads t))
                   (.writeNumberField "rollingCountCommandRejections"
                                      (.getRollingCount t (HystrixRollingNumberEvent/THREAD_POOL_REJECTED)))

                   (.writeNumberField "propertyValue_queueSizeRejectionThreshold"
                                      (-> t
                                          (.getProperties)
                                          (.queueSizeRejectionThreshold)
                                          (.get)))
                   (.writeNumberField "propertyValue_metricsRollingStatisticalWindowInMilliseconds"
                                      (-> t
                                          (.getProperties)
                                          (.metricsRollingStatisticalWindowInMilliseconds)
                                          (.get)))
                   (.writeNumberField "reportingHosts"  1)
                   (.writeEndObject)))))

;; convenience functions
(defn cmd->json
  "Convert a Hystrix command object to it's JSON representation"
  [cmd]
  (encode cmd))
