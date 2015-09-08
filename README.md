# immutant-hystrix-stream

Clojure stuff for encoding [Hystrix](https://github.com/Netflix/Hystrix) commands and providing
a data stream via SSE in [Immutant](http://immutant.org) applications.

## Usage
```clojure
(ns my-immutant-app
    (:require [immutant-hystrix-stream.stream :as hs])
    (:require [com.netflix.hystrix.core :refer [defcommand]])
    (:require [immutant.web :as web]
              [immutant.scheduling :as sch]))

(defcommand my-job
 []
 (do-stuff-that-might-fail))

(defn -main
 [& args]
 (sch/schedule my-job :every [1 :second])
 (web/run hs/hystrix-stream :host "localhost" :port 8082 :path "/hystrix-stream"))
```

## Overview

This library serves a very specific purpose, so if you aren't using
Immutant and Hystrix together it probably doesn't do you any good.

If you are, however, this will let you create a stream of Hystrix
metrics (presumably from your
[Hystrix-clj](https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-clj)
commands) for consumption by the [Hystrix
Dashboard](https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard).



## License

Copyright © 2015 Josh Rotenberg

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
