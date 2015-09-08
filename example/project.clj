(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main example.core
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.immutant/immutant "2.1.0"]
                 [compojure "1.3.4"]
                 [ring/ring-devel "1.3.1"]
                 [immutant-hystrix-stream "0.1.0"]
                 [com.netflix.hystrix/hystrix-clj "1.4.14"]])
