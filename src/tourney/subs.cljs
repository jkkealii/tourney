(ns tourney.subs
  (:require [re-frame.core :refer [reg-sub]]
            [clojure.string :refer [join]]))

(reg-sub :contestants   #(:contestants %))
(reg-sub :rounds        #(:rounds %))
(reg-sub :winner        #(:winner %))
(reg-sub :current-round #(:current-round %))
(reg-sub :bye           #(:bye %))
(reg-sub :file-name     #(:file-name %))
(reg-sub :file-error    #(:file-error %))

(reg-sub
  :file-error-msg
  :<- [:file-error]
  (fn [error _]
    (case error
      :file-type "Wrong file type, need CSV"
      :none      "No contestants provided"
      :need-more "More than two contestants required")))

(reg-sub
  :bye-contestants-delimited
  :<- [:bye]
  :<- [:contestants]
  (fn [[bye-contestant-ids contestants] _]
    (join ", "
      (map
        #(get contestants %)
        bye-contestant-ids))))

(reg-sub
  :current-round-completable?
  :<- [:current-round]
  :<- [:rounds]
  (fn [[current-round rounds] _]
    (let [current-matches (get rounds current-round)]
      (every?
        (fn [{:keys [winner]}]
          (some? winner))
        current-matches))))

(reg-sub
  :winner-name
  :<- [:winner]
  :<- [:contestants]
  (fn [[winner-id contestants] _]
    (get contestants winner-id)))
