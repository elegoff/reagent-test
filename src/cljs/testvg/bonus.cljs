(ns testvg.bonus
  (:require
   [reagent.core :as reagent :refer [atom]]
   [testvg.common :refer [gohome settitle]]
   ))

(defn get-counter [counters id]
  (first (filter #(= id (:id %)) @counters)))

(defn increment-time! [counters id]
  (swap! counters (fn [t]
                  (mapv #(if (= id (:id %))
                           (update % :elps inc)
                           %) t))))

(defn add-new-counter! [counters id]
  (swap! counters conj {:id id
                     :elps 0
                      :chrono-id (js/setInterval #(increment-time! counters id) 1000)
                      :running? true}))

(defn start-counter! [counters id]
  (swap! counters (fn [t]
                  (mapv #(if (= id (:id %))
                           (assoc %
                             :chrono-id (js/setInterval (fn [] (increment-time! counters id)) 1000)
                             :running? true) %) t))))

(defn stop-counter! [counters id]
  (js/clearInterval (:chrono-id (get-counter counters id)))
  (swap! counters (fn [t]
                  (mapv #(if (= id (:id %))
                           (assoc % :running? false) %) t))))

(defn remove-counter! [counters id]
  (let [c (get-counter counters id)]
    (when (:running? c)
      (js/clearInterval (:chrono-id c))))
  (swap! counters (
                   fn [c] (vec (remove #(= (:id %) id) c)))))

(defn bonus-page []
  (let [next-id (atom 0)
        counters (atom [])]
    (fn []
       
      [:div {:class "container"}
       [gohome]
       [settitle "Bonus"]
      
       [:button {
                 :class    "btn btn-default navbar-btn"
                 :on-click #(do (add-new-counter! counters @next-id)
                              (swap! next-id inc))} "Add a new counter"]
       (for [c @counters]
         ^{:key (:id c)}
         [:div.timer
          [:div {:class "row"} [:span {:class "label label-success"} "Seconds Elapsed : " [:span {:class "badge"} (:elps c)]]]
          [:button {:class    "btn btn-default navbar-btn"
                    :on-click #(if (:running? c)
                                 (stop-counter! counters (:id c))
                                 (start-counter! counters (:id c)))
                    }
           (if (:running? c) "Stop" "Start")]
          [:button
           {:class    "btn btn-default navbar-btn"
            :on-click #(remove-counter! counters (:id c))} "Remove"]])
       

       ])))
