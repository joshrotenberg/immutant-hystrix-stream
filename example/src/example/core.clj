(ns example.core
  "Some basic examples of using hystrix with immutant and exposing the metrics via immutant-hystrix-stream"
  (:require [immutant.web :as web]
            [immutant.scheduling :as sch]
            [immutant.web.middleware :as mw])
  (:require [immutant-hystrix-stream.stream :as hs])
  (:require [com.netflix.hystrix.core :refer [defcommand]])
  (:require 
            [compojure.core :refer (GET defroutes)]
            [ring.util.response :refer (response redirect content-type)])
  (:gen-class))

;; define a standalone counter fallback function to run if our main counter command fails (which it will). this might be a generic, smarter error
;; handler or something like that. here we still increment the counter so we only have failures when the count % 3 == 0 (see below)
(defn counter-fb
  "A fallback function to do something if our main handler throws an exception"
  [{session :session}]
  (let [count (:count session 0)
        session (assoc session :count (inc count))]
    (println "oops, session blew up")
    (-> (response "an error occurred")
        (assoc :session session))))

;; wrap a ring handler function in a hystrix command for more fault tolerance. this one just increments a counter (pulled from the immutant
;; feature demo), but fails any time the counter is divisible by 3
(defcommand counter
    "An example manipulating session state from
     https://github.com/ring-clojure/ring/wiki/Sessions, wrapped in a hystrix command"
    {:hystrix/fallback-fn counter-fb
     :hystrix/group-key "Web"
     :hystrix/command-key "Whatever"}
    [{session :session}]
    (let [count (:count session 0)
          session (assoc session :count (inc count))]
        (println "counter =>" count)
      (if (= 0 (mod count 3)) ;; a nasty bug that makes this handler fail every time the counter % 3 == 0
        (throw (Exception. "woof"))
        (-> (response (str "You accessed this page " count " times\n"))
            (assoc :session session)))))

(defroutes routes
  (GET "/counter" [] counter))

;; the next four commands represent jobs that we'll start with immutant's scheduling. each one fails at a different rate, and show various
;; other features of a hystrix command
(defcommand fails-often
  {:hystrix/fallback-fn (constantly 10) ;; the fallback will get called every time we divide by zero, so we won't see the exceptions 
   :hystrix/group-key "Jobs"} 
  []
  (let [r (rand-int 2)]
    (quot 10 r)))

(defcommand fails-sometimes
  {:hystrix/fallback-fn (constantly 10) ;; same as above
   :hystrix/command-key "Sometimes" ;; give this command a non-default name
   :hystrix/group-key "Jobs"} 
  []
  (let [r (rand-int 6)]
    (quot 10 r)))

(defcommand fails-rarely
  [] ;; no hystrix meta data here, so when this fails we should see the exception being thrown
  (let [r (rand-int 20)]
    (quot 10 r)))

(defcommand fails-never
  {:hystrix/command-key "IAmPerfect"
   :hystrix/group-key "Jobs"}
  [] ;; bulletproof, bug free code here
  (quot 10 1))

(defn -main
  [& args]

  ;; schedule our jobs
  (doseq [f [fails-often fails-sometimes fails-rarely fails-never]]
    (sch/schedule f :every 200))

  ;; fire up our web app stuff. request this a few times and take a look at the metrics: curl http://localhost:8081/counter
  (web/run
    (-> routes
               (immutant.web.middleware/wrap-session {:timeout 20}))
    (merge {:host "localhost" :port 8081}))
  ;; and run our hystrix metrics stream on a separate port (you don' have to do this, i guess). hs/hystrix-stream is just a normal ring handler func
  ;; and should work like any other that you'd use. curl http://localhost:8082/hystrix-stream 
  (web/run hs/hystrix-stream :host "localhost" :port 8082 :path "/hystrix-stream"))
