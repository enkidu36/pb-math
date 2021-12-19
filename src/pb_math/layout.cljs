(ns pb-math.layout
  (:require [reagent.core :as r]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [goog.object :as gobj]))

(set! *warn-on-infer* true)

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js
                                    {:main (gobj/get (.-blue ^js/Mui.Colors mui-colors) 100)}}}))

(defn custom-styles [^js/Mui.Theme theme]
  #js {:button    #js {:margin (.spacing theme 1)}
       :textField #js {:width       200
                       :marginLeft  (.spacing theme 1)
                       :marginRight (.spacing theme 1)}})

(def with-custom-styles (withStyles custom-styles))

(defn active-link [route-name current-page]
  (if (= route-name current-page)
    "underline"
    "none"))

(defn menu [route-links current-page]
  [:> mui/AppBar
   {:position "static"}
   [:> mui/Toolbar
    (for [route route-links
          :let [route-name (:link route)
                text (:text route)]]
      ^{:key route-name} [:> mui/Typography
                          {:variant "h6"
                           :style   {:padding-right 30}}
                          [:a {:href  route-name
                               :style {:text-decoration (active-link route-name current-page)}}
                           text]])]])

(defn header [route-links current-page]
  [:<>
   [:> mui/MuiThemeProvider {:theme custom-theme}
    [:> mui/Grid {:container true :direction "row" :justify "center"}
     [menu route-links current-page]]]])

(defn footer []
  [:<>
   [:> mui/MuiThemeProvider {:theme custom-theme}
    [:> mui/AppBar {:position "static"}
     [:> mui/Toolbar
      [:> mui/Typography {:variant "h6"} "Footer"]]]]])
