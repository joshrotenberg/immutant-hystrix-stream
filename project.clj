(defproject immutant-hystrix-stream "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:test
             { :dependencies [[com.netflix.hystrix/hystrix-clj "1.3.1"]]}}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.immutant/web "2.0.2"]
                 [com.netflix.hystrix/hystrix-core "1.4.12"]
                 [cheshire "5.4.0"]])
