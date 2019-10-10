(ns tourney.utils
  (:require [goog.labs.format.csv :as csv]
            [clojure.core.async :refer [chan put! go-loop <!]]
            [re-frame.core :refer [dispatch-sync]]))

(defn make-matches [contestants]
  (->> contestants
    (partition 2)
    (reduce
     (fn [all [opp1 opp2]]
       (conj all
             {:opp1   opp1
              :opp2   opp2
              :winner nil}))
     [])))

(defn next-highest-power-2 [input]
  "Return the next power of 2 greater than or equal to input"
  (js/Math.pow 2 (js/Math.ceil (/ (js/Math.log input) (js/Math.log 2)))))

(def first-file
  "Transducer that accepts input change events and gets the first selected file. Also note that we clear the target value. This allows re-uploading the same file."
  (map
    (fn [e]
      (let [target (.-currentTarget e)
            file   (-> target .-files (aget 0))]
        (set! (.-value target) "")
        file))))

(def extract-result
  "Transducer that accepts a `FileReader` onload event, gets the string contents, parses the CSV and converts the result to ClojureScript data structures."
  (map #(-> % .-target .-result csv/parse js->clj)))

(def upload-reqs (chan 1 first-file))
(def file-reads (chan 1 extract-result))

(defn put-upload [e]
  (put! upload-reqs e))

(go-loop []
  (let [reader (js/FileReader.)
        file   (<! upload-reqs)]
    (dispatch-sync [:save-file-name (.-name file)])
    (set! (.-onload reader) #(put! file-reads %))
    (.readAsText reader file)
    (recur)))

(go-loop []
  (dispatch-sync [:save-file-data (<! file-reads)])
  (recur))
