(defproject immutant-hystrix-stream "0.1.0"
  :description "Expose a metrics event/text-stream for consumption by the Hystrix Dashboard in Immutant apps"
  :url "https://github.com/joshrotenberg/immutant-hystrix-stream"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev
             {:dependencies [[com.netflix.hystrix/hystrix-clj "1.4.14"]
                             [clj-http "0.9.1"]]
              :plugins [[lein-cloverage "1.0.6"]]}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.immutant/web "2.0.2"]
                 [com.netflix.hystrix/hystrix-core "1.4.14"]
                 [cheshire "5.4.0"]])
