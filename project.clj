(defproject immutant-hystrix-stream "0.1.0-SNAPSHOT"
  :description "Expose a metrics event/text-stream for consumption by the Hystrix Dashboard in Immutant apps"
  :url "https://github.com/joshrotenberg/immutant-hystrix-stream"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:test
             {:dependencies [[com.netflix.hystrix/hystrix-clj "1.4.12"]]}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.immutant/web "2.0.2"]
                 [com.netflix.hystrix/hystrix-core "1.4.12"]
                 [cheshire "5.4.0"]
                 [com.netflix.hystrix/hystrix-clj "1.4.12"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]])
