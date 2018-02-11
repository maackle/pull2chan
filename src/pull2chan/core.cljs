(ns pull2chan.core
  (:require [cljs.core.async :refer (chan put! take! close! timeout)])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def pull-stream (js/require "pull-stream"))

(defn pull->chan
  "Convert a pull-stream source into a channel"
  ([source] (pull->chan (chan) source))
  ([ch source]
   (source nil (fn read [err val]
                 (if err
                   (close! ch)  ; TODO: really?
                   (go
                    (put! ch val
                          #(if %
                             (source nil read)
                             (close! ch)))))
                 ))
   ch
   ))

(defn chan->pull
  "Convert a channel into a pull-stream source"
  [ch]
  (fn [end f]
    (if end
      (f end)
      (take! ch
             (fn [v]
               (if (nil? v) ; then channel has been closed
                 (f true)   ; and we should tell the pull-stream so (only once)
                 (f nil v)  ; otherwise pass on the value from the channel
                 ))))))
