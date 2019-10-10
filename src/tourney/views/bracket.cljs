(ns tourney.views.bracket
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn match-box-fn [round-index]
  (fn [index {:keys [opp1 opp2 winner]}]
    (let [contestants    (subscribe [:contestants])
          current-round  (subscribe [:current-round])
          round-current? (= round-index @current-round)]
      ^{:key (str opp1 "-" opp2)}
      [:ul.list-group
       [:li.list-group-item.d-flex.justify-content-between.align-items-center (when (= winner opp1) {:class "active"})
        (get @contestants opp1)
        (when round-current?
          [:button.btn.btn-outline-success {:on-click #(dispatch [:choose-winner index opp1])} "Win"])]
       [:li.list-group-item.d-flex.justify-content-between.align-items-center (when (= winner opp2) {:class "active"})
        (get @contestants opp2)
        (when round-current?
          [:button.btn.btn-outline-success {:on-click #(dispatch [:choose-winner index opp2])} "Win"])]])))

(defn matches-column [index matches]
  (let [match-box (match-box-fn index)]
    ^{:key index}
    [:div.col-auto
     ((comp doall map-indexed)
      match-box
      matches)
     (when (= index @(subscribe [:current-round]))
       [:button.btn.btn-outline-warning
        {:on-click #(dispatch [:complete-round])
         :disabled (not @(subscribe [:current-round-completable?]))}
        "Complete round " (+ index 1)])]))

(defn bracket-view []
  (let [rounds (subscribe [:rounds])
        winner (subscribe [:winner-name])]
    [:div.container
     [:h2 "Bracket"]
     [:div.row.align-items-center
      ((comp doall map-indexed)
       matches-column
       @rounds)
      (when (some? @winner)
        [:div.col
         [:h1.display-4 "Congratulations to our winner, " @winner "!"]])]
     [:div.row
      [:div.col-auto
       [:p "The following contestants have a round 1 bye: " @(subscribe [:bye-contestants-delimited])]]]]))
