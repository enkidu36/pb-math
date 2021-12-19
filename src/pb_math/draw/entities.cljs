(ns pb-math.draw.entities
  (:require [monet.canvas :as c]))

(defn point [[x y]  color]
  (let [size 4
        ctr-adj (/ size 2)
        x-ctr (-> x  (- ctr-adj))
        y-ctr (-> y  (- ctr-adj))]
    (c/entity
      {:x x-ctr :y y-ctr :h size :w size :color color}
      nil
      (fn [ctx {:keys [color] :as val}]
        (-> ctx
            (c/save)
            (c/fill-style color)
            (c/translate 300 300)
            (c/scale 1 -1)
            (c/fill-rect val)
            (c/restore))))))

(defn rect [[x y] [h w] color]
  (c/entity
    {:x x :y y :h h :w w :color color}
    nil
    (fn [ctx {:keys [color] :as val}]
      (-> ctx
          (c/save)
          (c/fill-style color)
          (c/fill-rect val)
          (c/restore)))))

(defn line [[x y] [x2 y2] width color]
  (c/entity
    {:x x :y y :x2 x2 :y2 y2 :width width :color color}
    nil
    (fn [ctx {:keys [x y x2 y2 color width]}]
      (-> ctx
          (c/save)
          (c/begin-path)
          (c/move-to x y)
          (c/line-to x2 y2)
          (c/stroke-style color)
          (c/stroke-width width)
          (c/stroke)
          (c/restore)))))
