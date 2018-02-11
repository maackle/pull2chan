(ns ^:figwheel-always pull2chan.tests
  (:require [cljs.core.async :refer (chan >! <! put! close! timeout)]
            [figwheel.client :as fw]
            cljs.test
            [pull2chan.core :refer (pull->chan chan->pull)])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [cljs.test :refer [deftest is testing run-tests async]]))

(def pull (js/require "pull-stream"))

(deftest test-pull->chan
  (async done
         (let [source (. pull (values #js [1 2 3]))
               ch (pull->chan source)]
           (go (is (= (<! ch) 1))
               (is (= (<! ch) 2))
               (is (= (<! ch) 3))
               (is (= (<! ch) nil)) ; channel is closed after being drained
               (done)
               ))))

(deftest test-chan->pull
  (async done
         (let [ch (chan)
               take-count (atom 0)
               source (chan->pull ch)
               vals [1 2 3 4 5 6 7 8 9]
               sink (. pull (collect (fn [err actual]
                                       (is (= (js->clj actual) vals))
                                       (done))))
               ]
           (go
            (>! ch 1)
            (>! ch 2)
            (>! ch 3)
            (>! ch 4)
            (>! ch 5)
            (>! ch 6)
            (>! ch 7)
            (>! ch 8)
            (>! ch 9)
            (close! ch)
            (is (not (>! ch "won't happen")))
            )
           (pull source sink)
           )))

(deftest test-chan->pull->chan
  (async done
         (let [ch1 (chan)
               source (chan->pull ch1)
               ch2 (pull->chan source)]
           (go (>! ch1 1)
               (>! ch1 2)
               (close! ch1)
               )
           (go (is (= (<! ch2) 1))
               (is (= (<! ch2) 2))
               (is (= (<! ch2) nil))
               (done)
               ))))

(deftest test-pull->chan->pull
  (async done
         (let [take-count (atom 0)
               nums (vec (range 10000))
               source (-> (. pull (values (clj->js nums)))
                          pull->chan
                          chan->pull)
               sink (. pull (collect
                             (fn [err v]
                               (is (= nums (js->clj v)))
                               (done)
                               )))]
           (pull source sink)
           )))

(enable-console-print!)
(run-tests)
