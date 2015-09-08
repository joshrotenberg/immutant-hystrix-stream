# immutant-hystrix-stream

[![Build Status](https://travis-ci.org/joshrotenberg/immutant-hystrix-stream.svg)](https://travis-ci.org/joshrotenberg/immutant-hystrix-stream)

[![Coverage Status](https://coveralls.io/repos/joshrotenberg/immutant-hystrix-stream/badge.svg?branch=master&service=github)](https://coveralls.io/github/joshrotenberg/immutant-hystrix-stream?branch=master)

[![Clojars Project](http://clojars.org/immutant-hystrix-stream/latest-version.svg)](http://clojars.org/immutant-hystrix-stream)

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

See the [example](https://github.com/joshrotenberg/immutant-hystrix-stream/tree/master/example) application for a quick usage overview.

## Quickstart

The best way to get an idea of whats going on is to take a look at the
following code, and then hook it up to the Hystrix Dashboard and check
out all the eye candy. Follow these steps:

```
# Run the example app
cd immutant-hystrix-stream/example
lein run

# In another shell, install wildfly and the dashboard
WILDFLY_VERSION=9.0.1.Final
HYSTRIX_VERSION=1.4.14

# Install WildFly
wget http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip
unzip wildfly-$WILDFLY_VERSION.zip

# Download/Install the Hystrix Dashboard
cd wildfly-$WILDFLY_VERSION/standalone/deployments/
wget http://repo2.maven.org/maven2/com/netflix/hystrix/hystrix-dashboard/$HYSTRIX_VERSION/hystrix-dashboard-$HYSTRIX_VERSION.war
cd -

# Start Wildfly
wildfly-$WILDFLY_VERSION/bin/standalone.sh

# In your browser, navigate to http://localhost:8080/hystrix-dashboard-$HYSTRIX_VERSION 
# and enter the url of your running apps hystrix stream: http://localhost:8082/hystrix-stream and click Monitor Stream
# Use another browser tab or curl to request the counter entry point a few times: curl http://localhost:8081/counter

```

## License

Copyright © 2015 Josh Rotenberg

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
