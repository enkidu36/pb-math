(ns pb-math.view.home-page
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [goog.dom :as gdom]
            [monet.canvas :as mc]
            [pb-math.draw.entities :as entities]
            [pb-math.draw.util :as util]))

(defn get-canvas []
  (->
    "canvas"
    (gdom/getElement)
    (mc/init "2d")))

(def dino [[5 4] [3 1] [1 2] [-1 5] [-2 5]
           [-3 4] [-4 4] [-5 3] [-5 2] [-2 2]
           [-5 1] [-4 0] [-2 1] [-1 0] [0 -3]
           [-1 -4] [1 -4] [2 -3] [1 -2] [3 -1] [5 1] [0 0]])

;(defn grid-axis! [canvas line-width color]
;  (let [width (util/canvas-attr canvas "width")
;        height (util/canvas-attr canvas "height")
;        x-start [(- 0 (/ width 2)) 0]
;        x-end [(+ (/ width 2)) 0]
;        y-start [0 (- 0  (/ height 2))]
;        y-end [0 (+ (/ height 2))]]
;    (mc/add-entity canvas)))

(defn draw [monet-canvas color]
  (let [ctx (:ctx monet-canvas)
        canvas-width (util/canvas-attr monet-canvas "width")
        canvas-height (util/canvas-attr monet-canvas "height")
        ctr-x (/ canvas-width 2)
        ctr-y (/ canvas-height 2)
        line-width .01]
    (mc/translate ctx ctr-x ctr-y)
    (mc/scale ctx 1 -1)
    (mc/add-entity monet-canvas (gensym ":x-quad1")
                   (entities/line [0 0] [60 40] line-width "white"))
    (mc/add-entity monet-canvas (gensym ":x-axis1")
                   (entities/line [0 0] [(- 0 ctr-x) 0] line-width color))
    (mc/add-entity monet-canvas (gensym ":x-axis2")
                   (entities/line [0 0]  [(+ 0 ctr-x) 0] line-width color))
    (mc/add-entity monet-canvas (gensym ":y-axis1")
                   (entities/line [0 0] [0 (- 0 ctr-y)] line-width color))
    (mc/add-entity monet-canvas (gensym ":y-axis2")
                   (entities/line [0 0] [0 (+ 0 ctr-y)] line-width color))
    (dotimes (pt (count dino))
      (mc/add-entity monet-canvas (gensym ":pt")
                     (entities/point (nth dino pt) color)))))


(defn canvas-component [draw-fn]
  (r/create-class
    {:display-name "canvas"
     :component-did-mount
                   (fn [this] 
                     (println "canvas did mount")
                     (draw (get-canvas) "yellow"))
     :reagent-render
                   (fn [draw-fn]
                     [:canvas#canvas {:width 600 :height 600 :style {:border "1px" "solid" "yellow"}}])}))

(defn home-page []
  [:<>
   [:> mui/Grid
    {:container true
     :spacing   20 :justify "space-around"
     :style     {:padding 50}}
    [:> mui/Grid
     {:item  true
      :style {:background "blue"}}
     [canvas-component draw]]]])
