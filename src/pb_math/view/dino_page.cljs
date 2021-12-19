(ns pb-math.view.dino-page
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            [goog.dom :as gdom]
            [monet.canvas :as mc]
            [re-frame.core :as re-frame]
            [reagent.core :as r]
            [pb-math.draw.util :as utils]
            [pb-math.draw.entities :refer [point line rect]]))

(def dino [[5 4] [3 1] [1 2] [-1 5] [-2 5]
           [-3 4] [-4 4] [-5 3] [-5 2] [-2 2]
           [-5 1] [-4 0] [-2 1] [-1 0] [0 -3]
           [-1 -4] [1 -4] [2 -3] [1 -2] [3 -1] [5 1] [0 0]])

(def chart
  (atom {
         :background "rgba(0, 255, 0, 0.5)"
         :padding 30
         :horzontal-scale 14
         :vertical-scale 14
         :grid-line-color "black"
         :grid-line-size 0.1}))

(defn calc-pos [pos scale axis-val]
  (+ (:padding @chart) (* pos (/ axis-val scale))))

(defn make-line [[x y] [h w]]
  (line [x y] [h w] (:grid-line-size @chart) (:grid-line-color @chart)))

(defn get-chart-dim [dim]
  (let [canvas (:canvas @chart)
        canvas-h (utils/canvas-attr canvas dim)
        padding (:padding @chart)]
    (- canvas-h (* padding 2))))

(defn calc-point [[x y]]
  (let [h-scale (:horzontal-scale @chart)
        w-scale (:vertical-scale @chart)]
    [(* x (/ (get-chart-dim "width") w-scale))
     (* y (/ (get-chart-dim "height") h-scale))]))

(defn make-point [[x y]]
  (point (calc-point [x y]) "black"))

(defn add-axis []
  (mc/add-entity (:canvas @chart) "y-axis" (make-line [300 0] [300 600]))
  (mc/add-entity (:canvas @chart) "x-axis" (make-line [0 300] [600 300])))

(defn add-background []
  (let [padding (:padding @chart)
        h (get-chart-dim "height")
        w (get-chart-dim "width")
        background (:background @chart)]
    (mc/add-entity (:canvas @chart) "backgrounddddddddddddddddddd" (rect [padding padding] [h w] background))))

(defn remove-entities [entities]
  (dotimes [n (count entities)]
    (mc/remove-entity (:canvas @chart) (nth entities n))))

(defn add-gridlines []
  (let [canvas (:canvas @chart)
        padding (:padding @chart)
        h (get-chart-dim "height")
        w (get-chart-dim "width")
        h-scale (:horzontal-scale @chart)
        w-scale (:vertical-scale @chart)]
    (dotimes [n (+ w-scale 1)]
      (mc/add-entity canvas (str ":y-gl" n) (make-line [(calc-pos n w-scale w) padding] [(calc-pos n w-scale w) (+ padding h)])))
    (dotimes [n (+ h-scale 1)]
      (mc/add-entity canvas (str ":x-gl" n) (make-line [padding (calc-pos n h-scale h)] [(+ padding h) (calc-pos n h-scale h)])))))

(defn remove-gridlines []
  (remove-entities (mapv #(str ":y-gl" %) (range (+ 1 (:vertical-scale @chart)))))
  (remove-entities (mapv #(str ":x-gl" %) (range (+ 1 (:horzontal-scale @chart))))))

(defn init-chart []
  (let [init-controls @(re-frame/subscribe [::set-init-controls])]
    (when (::show-background init-controls)
      (add-background))
    (when (::show-axis init-controls)
      (add-axis))
    (when (::show-gridlines init-controls)
      (add-gridlines))))

(defn draw-chart []
  (let [canvas (:canvas @chart)]
    (init-chart)
    (dotimes [pt (count dino)]
      (mc/add-entity canvas (gensym ":pt") (make-point (nth dino pt))))))

(re-frame/reg-event-db
  ::set-init-controls
  (fn [db [_]]
    (js/console.log "set-controls")
    (assoc db ::chart-controls
              {::show-background true ::show-axis true ::show-gridlines true})))

(re-frame/reg-sub
  ::set-init-controls
  (fn [db]
    (::chart-controls db)))

(re-frame/reg-event-fx
  ::toggle-control
  (fn [{:keys [db]} [_ ctr-key]]
    (js/console.log "::toggle-control" ctr-key)
    (let [path [::chart-controls ctr-key]
          new-db (assoc-in db path (not (get-in db path)))]
      {:db new-db
       ::toggle (get-in new-db [::chart-controls])})))

(re-frame/reg-fx
  ::toggle
  (fn [control-db]
    (js/console.log "controls" control-db)
    (if (::show-background control-db)
      (add-background)
      (remove-entities ["background"]))
    (if (::show-axis control-db)
      (add-axis)
      (remove-entities ["y-axis" "x-axis"]))
    (if (::show-gridlines control-db)
      (add-gridlines)
      (remove-gridlines))))

(defn draw [canvas color]
  (let [ctx (:ctx canvas)]
    (swap! chart assoc :canvas canvas)
    (re-frame/dispatch [::set-init-controls])
    (draw-chart)))

(defn controls []
  (let [init-controls @(re-frame/subscribe [::set-init-controls])]
    (fn []
      (js/console.log "controls")
      [:> mui/FormGroup
       [:> mui/FormControlLabel
        {:label "Background"
         :control (r/as-element [:> mui/Checkbox (merge
                                                   {:on-click #(re-frame/dispatch [::toggle-control ::show-background])}
                                                   (when (::show-background init-controls) {:default-checked "true"}))])}]
       [:> mui/FormControlLabel
        {:label "Axis"
         :control (r/as-element [:> mui/Checkbox (merge
                                                   {:on-click #(re-frame/dispatch [::toggle-control ::show-axis])}
                                                   (when (::show-axis init-controls) {:default-checked "true"}))])}]
       [:> mui/FormControlLabel
        {:label "Gridlines"
         :control (r/as-element [:> mui/Checkbox (merge
                                                   {:on-click #(re-frame/dispatch [::toggle-control ::show-gridlines])}
                                                   (when (::show-gridlines init-controls) {:default-checked "true"}))])}]])))

(defn dino-page []
  (let [mounted (r/atom false)]
    (fn []
      [:<>
       [:> mui/Grid
        {:container true
         :spacing 40
         :justify "space-around"
         :style {:padding 50}}
        [:> mui/Grid {:item true}
         [utils/canvas-component draw]]
        [:> mui/Grid
         {:item true
          :spacing 22
          :justify ""
          :style {:background-color "inherit"}}
         [controls]]]])))