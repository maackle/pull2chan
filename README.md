pull-cljs-channel
=================

Seamlessly convert between [pull-stream](https://github.com/pull-stream/pull-stream) sources and ClojureScript core.async channels, preserving backpressure

## Example

Imports at top of file:

```cljs
(ns example
  (:require [cljs.core.async :refer (chan)]
            [pull2chan.core :refer (pull->chan chan->pull)])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def pull (js/require "pull-stream"))
```

pull-stream -> channel

```cljs
(let [source (. pull (values #js [1 2 3]))
      ch (pull->chan source)]
  (go (println (<! ch)) ; output: 1
      (println (<! ch)) ; output: 2
      (println (<! ch)) ; output: 3
      (println (<! ch)) ; output: nil (channel is closed after being drained)
      ))
```

channel -> pull-stream

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

See the [tests](./test/pull2chan/tests.cljs) for a few more examples
