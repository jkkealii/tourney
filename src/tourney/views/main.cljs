(ns tourney.views.main
  (:require [re-frame.core :refer [subscribe dispatch]]
            [tourney.utils :refer [put-upload]]
            [tourney.views.bracket :refer [bracket-view]]))

(defn upload-view []
  (let [file-name  (subscribe [:file-name])
        file-error (subscribe [:file-error])]
    [:div.container
     [:div.row.justify-content-center.align-items-center
      [:div.col-12
       [:p "Upload a CSV with contestant names separated by line breaks"]]
      [:div.col-auto
       [:span
        [:label#csv-upload.btn.btn-outline-primary.mb-0
         [:input.d-none {:type "file" :accept ".csv" :on-change put-upload}]
         "upload CSV of contestants"]]]
      (when (some? @file-name)
        [:div.col-auto
         [:div "Uploaded file: " @file-name]])
      (when (some? @file-error)
        [:div.col-auto
         [:div "ERROR WITH UPLOADED CSV:" @(subscribe [:file-error-msg])]])
      (when (and (some? @file-name) (nil? @file-error))
        [:div.col-auto
         [:button.btn.btn-outline-success {:on-click #(dispatch [:save-file-contestants])} "Save contestants"]])]]))

(defn app []
  [:main.pt-4
   [:h1 "SFOSL Tourney"]
   (if (empty? @(subscribe [:rounds]))
     [upload-view]
     [bracket-view])])
