(ns immutant-hystrix-stream.test.commands
  (:require [com.netflix.hystrix.core :refer [defcommand defcollapser instantiate]]))

(defcommand my-fn-command
  "A doc string"
  {:meta :data
   :hystrix/fallback-fn (constantly 500)}
  [a b]
  (+ a b))

(defcommand my-overload-fn-command
  "A doc string"
  {:meta :data
   :hystrix/fallback-fn (constantly 500)}
  ([a b]
   (+ a b))
  ([a b c]
   (+ a b c)))

(defcommand to-upper-batcher
  "a batch version of to-upper"
  [xs]
  (mapv #(.toUpperCase ^String %) xs))

(defcollapser to-upper-collapser
  "A collapser that dispatches to to-upper-batcher"
  (collapse [arg-lists]
            (instantiate #'to-upper-batcher (mapv first arg-lists)))
  (map [arg-lists uppered]
       uppered))
