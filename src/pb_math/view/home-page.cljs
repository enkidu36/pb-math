(ns pb-math.view.home-page
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]))

(defn home-page []
  [:<>
   [:> mui/Grid
    {:container true
     :spacing   20 :justify "space-around"
     :style     {:padding 50}}
    [:> mui/Grid
     {:item  true
      :style {:background "inherit"}}
     [:h1 "home page"]]]])
