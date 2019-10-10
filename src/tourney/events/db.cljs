(ns tourney.events.db
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]))

(defonce initial-state
  {:contestants   {}
   :rounds        []
   :bye           []
   :winner        nil
   :current-round 0
   :file-name     nil
   :file-data     nil
   :file-error?   nil})

(reg-event-fx
  :initialize-app
  (fn [{:keys [db]} _]
    {:db (merge db initial-state)}))
