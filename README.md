pull2chan
=================

Seamlessly convert between [pull-stream](https://github.com/pull-stream/pull-stream) sources and ClojureScript core.async channels, preserving backpressure

## Installation

Add `[maackle/pull2chan "0.1.0"]` to your project.clj `:dependencies`

## Examples

Imports at top of file:

```cljs
(ns example
  (:require [cljs.core.async :refer (chan <! >! close!)]
            [pull2chan.core :refer (pull->chan chan->pull)])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def pull (js/require "pull-stream"))
```

### pull-stream -> channel

```cljs
(let [source (. pull (values #js [1 2 3]))
      ch (pull->chan source)]
  (go (println (<! ch)) ; output: 1
      (println (<! ch)) ; output: 2
      (println (<! ch)) ; output: 3
      (println (<! ch)) ; output: nil (channel is closed after source is depleted)
      (println (<! ch)) ; output: nil (still closed...)
      ))
```

### channel -> pull-stream

```cljs
(let [ch (chan)
      source (chan->pull ch)
      sink (. pull (drain
                    #(println %)
                    #(println "done")))]
  (pull source sink)
  (go (>! ch 1)
      (>! ch 2)
      (>! ch 3)
      (close! ch))
  )

; output:
; 1
; 2
; 3
; done
```

### channel -> pull-stream -> channel -> pull-stream -> channel

Yes, you can go back and forth as much as you want!

See the [tests](./test/pull2chan/tests.cljs) for a few more examples

## Development

1. Run `[rlwrap] lein figwheel devserver` in one session
2. Run `node target/devserver.js` in another

To run tests with hot reloading: `lein doo node test`
