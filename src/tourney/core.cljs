(ns tourney.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch-sync]]
            [tourney.events.db]
            [tourney.views.main]
            [tourney.views.bracket]
            [tourney.fx]
            [tourney.cofx]
            [tourney.subs]))

(enable-console-print!)

(println "This text is printed from src/tourney/core.cljs. Go ahead and edit it and see reloading in action.")


(defn run []
  (dispatch-sync [:initialize-app])
  (reagent/render-component [tourney.views.main/app]
                            (. js/document (getElementById "app"))))

(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

(run)
