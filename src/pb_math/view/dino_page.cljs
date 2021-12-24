(ns pb-math.view.dino-page
  (:require (pb-math.components.chart :as chart)))

(def dino [[5 4] [3 1] [1 2] [-1 5] [-2 5]
           [-3 4] [-4 4] [-5 3] [-5 2] [-2 2]
           [-5 1] [-4 0] [-2 1] [-1 0] [0 -3]
           [-1 -4] [1 -4] [2 -3] [1 -2] [3 -1] [5 1]])

(defn draw []
  (chart/draw-lines dino 1 "green"))

(defn dino-page []
  [chart/chart-comp draw])
