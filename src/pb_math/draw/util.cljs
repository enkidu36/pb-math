(ns pb-math.draw.util
  (:require [monet.canvas :as mc]
            [reagent.core :as r]
            [goog.dom :as gdom]))

(defn get-canvas []
  (->
    "canvas"
    (gdom/getElement)
    (mc/init "2d")))

(defn canvas-attr [mc a]
  (.getAttribute (:canvas mc) a))

(defn canvas-ctr [mc]
  {:x (/ (canvas-attr mc "width") 2)
   :y (/ (canvas-attr mc "height") 2)})

(defn canvas-component
  ([draw-fn] (canvas-component draw-fn "red"))
  ([draw-fn color]
   (r/create-class
     {:display-name   "canvas"
      :component-did-mount
                      (fn [this]
                        (js/console.log "canvas did mount ")
                        (draw-fn (get-canvas) "orange"))
      :reagent-render (fn [mounted]
                        (js/console.log "reagent render ")
                        [:canvas#canvas {:width 600 :height 600 :style {:border (str "2px solid " color)}}])})))
