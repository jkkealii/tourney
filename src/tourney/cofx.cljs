(ns tourney.cofx
  (:require [re-frame.core :refer [reg-cofx]]
            [tourney.utils :refer [next-highest-power-2 make-matches]]))

(reg-cofx
  :prime-contestants
  (fn [{{:keys [file-data]} :db :as coeffects} _]
    (let [names       (flatten file-data)
          contestants (reduce
                       (fn [store name]
                         (assoc store (str (random-uuid)) name))
                       {}
                       names)]
      (assoc coeffects :contestants contestants))))

(reg-cofx
  :generate-first-round
  (fn [{{:keys [contestants]} :db :as coeffects} _]
    (let [contestant-count    (count contestants)
          bye-count           (- (next-highest-power-2 contestant-count) contestant-count)
          contestant-ids      ((comp vec keys) contestants)
          playing-contestants (subvec contestant-ids 0 (- contestant-count bye-count))
          bye-contestants     (subvec contestant-ids (- contestant-count bye-count))
          matches             (make-matches playing-contestants)]
      (assoc coeffects :bye-contestants bye-contestants :matches matches))))

(reg-cofx
  :generate-next-round
  (fn [{{:keys [current-round rounds bye]} :db :as coeffects} _]
    (let [current-matches (get rounds current-round)
          winning-ids     (map
                            (fn [{:keys [winner]}]
                              winner)
                            current-matches)
          contestant-ids  (cond-> winning-ids
                            (zero? current-round) (into bye))
          matches         (make-matches contestant-ids)
          winner          (when (= (count contestant-ids) 1)
                            (first contestant-ids))]
      (assoc coeffects :matches matches :winner winner))))
