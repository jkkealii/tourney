(ns tourney.fx
  (:require [re-frame.core :refer [reg-event-fx reg-event-db trim-v inject-cofx]]))

(reg-event-db
  :save-file-name
  trim-v
  (fn [db [file-name]]
    (let [not-csv? (nil? (re-matches #".+.csv" file-name))]
      (cond-> (assoc db :file-name file-name)
        not-csv? (assoc :file-error :file-type)))))

(reg-event-fx
  :save-file-data
  trim-v
  (fn [{{:keys [file-error] :as db} :db} [file-data]]
    (js/console.log "FILE DATA" (clj->js file-data))
    (cond-> {:db (assoc db :file-data file-data)}
      (some? file-error) (assoc :dispatch [:file-validation]))))

(reg-event-db
  :file-validation
  (fn [{:keys [file-data] :as db} _]
    (let [need-more?  (< (count file-data) 2)
          count-error (cond
                        (empty? file-data) :none
                        need-more?         :need-more)]
      (js/console.log "FILE VALIDATION, COUNT ERROR" (clj->js count-error))
      (assoc-in db [:file-error] count-error))))

(reg-event-fx
  :save-file-contestants
  (inject-cofx :prime-contestants)
  (fn [{:keys [db contestants]} _]
    {:db       (assoc db :contestants contestants)
     :dispatch [:start-tourney]}))

(reg-event-fx
  :start-tourney
  (inject-cofx :generate-first-round)
  (fn [{:keys [db bye-contestants matches]} _]
    {:db (assoc db :bye bye-contestants :rounds (vector matches))}))

(reg-event-db
  :choose-winner
  trim-v
  (fn [{:keys [current-round] :as db} [index winner-id]]
    (assoc-in db [:rounds current-round index :winner] winner-id)))

(reg-event-fx
  :complete-round
  (inject-cofx :generate-next-round)
  (fn [{:keys [db matches winner]} _]
    {:db (cond-> (update db :current-round inc)
            (some? winner) (assoc :winner winner)
            (nil? winner) (update :rounds #(conj % matches)))}))
