(ns pb-math.app
  (:require
    [goog.dom :as gdom]
    [re-frame.core :as re-frame]
    [reagent.dom :as rdom]
    [reitit.core :as r]
    [reitit.coercion.spec :as rss]
    [reitit.frontend :as rf]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]
    ["@material-ui/core" :as mui]
    [pb-math.layout :refer [header footer]]
    [pb-math.view.home-page :refer [home-page]]
    [pb-math.view.dino-page :refer [dino-page]]))

;;; Events ;;;

(re-frame/reg-event-db
  ::initialize-db
  (fn [db _]
    (if db
      db
      {:current-route nil})))

(re-frame/reg-event-fx
  ::push-state
  (fn [db [_ & route]]
    {:push-state route}))

(re-frame/reg-event-db
  ::navigated
  (fn [db [_ new-match]]
    (let [old-match (:current-route db)
          controllers (rfc/apply-controllers (:controllers old-match) new-match)]
      (assoc db :current-route (assoc new-match :controllers controllers)))))

;;; Subscriptions ;;;

(re-frame/reg-sub
  ::current-route
  (fn [db]
    (:current-route db)))

;; Triggering navigation from events.

(re-frame/reg-fx
  :push-state
  (fn [route]
    (apply rfe/push-state route)))

;;; Routes ;;;

(defn href
  "Return relative url for given route. Url can be used in HTML links."
  ([k]
   (href k nil nil))
  ([k params]
   (href k params nil))
  ([k params query]
   (rfe/href k params query)))

(def routes
  ["/"
   [""
    {:name      ::home
     :view      home-page
     :link-text "Home"
     :controllers
                [{;; Do whatever initialization needed for home page
                  ;; I.e (re-frame/dispatch [::events/load-something-with-ajax])
                  :start (fn [& params] (js/console.log "Entering home page"))
                  ;; Teardown can be done here.
                  :stop  (fn [& params] (js/console.log "Leaving home page"))}]}]
   ["ch2"
    {:name      ::chapter-2
     :view      dino-page
     :link-text "2D Graphics"
     :controllers
                [{:start (fn [& params] (js/console.log "Entering 2D Graphics"))
                  :stop  (fn [& params] (js/console.log "Leaving 2D Graphics"))}]}]])


(defn on-navigate [new-match]
  (when new-match
    (re-frame/dispatch [::navigated new-match])))

(def router
  (rf/router
    routes
    {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
    router
    on-navigate
    {:use-fragment true}))

(defn route-links [router]
  (map
    (fn [route-name]
      (let [route (r/match-by-name router route-name)
            text (-> route :data :link-text)]
        {:link (href route-name) :text text}))
    (r/route-names router)))

(defn router-component [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::current-route])]
    [:div]
    [:> mui/CssBaseline
     [header (route-links router) (-> current-route :data :name href)]
     (when current-route
       [(-> current-route :data :view)])
     [footer]]))

;;; Setup ;;;

(def debug? ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")))

(defn init []
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [::initialize-db])
  (dev-setup)
  (init-routes!)                                            ;; Reset routes on figwheel reload
  (rdom/render [router-component {:router router}]
               (gdom/getElement "app")))

(init)
