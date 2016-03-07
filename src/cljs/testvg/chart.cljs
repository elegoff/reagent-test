(ns testvg.chart
    (:require [reagent.core :as r :refer [atom]]
              [testvg.common :refer [gohome settitle]]
              [ajax.core :refer [GET]]
              ))



(defn render1 []
  [:div#chart1 {:style {:min-width "50%" :max-width "100%" 
                        :height "400px" :margin "0 auto"}}])


(defn render2 []
  [:div#chart2 {:style {:min-width "50%" :max-width "100%" 
                        :height "400px" :margin "0 auto"}}])



(defn get-data
  [myhandler]
  (GET "http://api.vigiglobe.com/api/statistics/v1/volume?project_id=vigiglobe-Earthquake"
       {:response-format :json 
        :keywords? true
        :handler myhandler}))







(defn draw-chart1
  [data]
  (let [serie (-> (.highcharts (js/$ "#chart1"))
                   .-series
                   (aget 0))
        new-time (-> data :data :messages first first js/Date. .getTime)
        new-value (-> data :data :messages first second)
        ]
        
    
    (.addPoint serie 
               (clj->js [new-time new-value]) 
               true 
               (if (< 360 (count (.-data serie)))
                 true
                 false))))


(defn draw-chart2
  [data]
  (let [serie (-> (.highcharts (js/$ "#chart2"))
                   .-series
                   (aget 0))
        new-time (-> data :data :messages first first js/Date. .getTime)
        new-value (-> data :data :messages first second)
        ]
        
    
    (.addPoint serie 
               (clj->js [new-time new-value]) 
               true 
               (if (< 120 (count (.-data serie)))
                 true
                 false))))



  


(def timer1 (atom nil))

(def timer2 (atom nil))



(defn start-timer
  [timer draw-fn duration]
  (let [timer-id (js/setInterval #(get-data draw-fn) duration)]
    (reset! timer timer-id)))

(def start-timer1
  (partial start-timer timer1 draw-chart1 10000)
  )

(def start-timer2
  (partial start-timer timer2 draw-chart2 30000)
  )





(defn stop-timer1
  []
  (when @timer1
    (do
      (js/clearInterval @timer1)
      (reset! timer1 nil))))

(defn stop-timer2
  []
  (when @timer2
    (do
      (js/clearInterval @timer2)
      (reset! timer2 nil))))


(def config1
  {:chart {:type "spline"
           :marginRight 10
           :events {:load start-timer1 }}
   
   :title {:text "Volume (10 sec)"}
   :xAxis {:type "datetime"
           }
   :yAxis {:title {:text "Nb Tweets"}
           :plotLines [{:value 0}]}
   :legend {:enabled false}
   :exporting {:enabled false}
   :series [{:data []}]
   }
  )


(def config2
  {:chart {:type "spline"
           :marginRight 10
           :events {:load start-timer2}}
   
   :title {:text "Volume (30 sec)"}
   :xAxis {:type "datetime"
           }
   :yAxis {:title {:text "Nb Tweets"}
           :plotLines [{:value 0}]}
   :legend {:enabled false}
   :exporting {:enabled false}
   :series [{:data []}]
   }
  )





(defn graph-component-mount [div config draw-fn]
  (.highcharts (js/$ div)
               (clj->js config))
  (get-data draw-fn))  


(def graph-component-mount1
  (partial graph-component-mount "#chart1" config1 draw-chart1)
  
  )

(def graph-component-mount2
  (partial graph-component-mount "#chart2" config2 draw-chart2)
  
  )





(defn chart-component [render component-did-mount component-will-unmount]
  (r/create-class {:reagent-render render
                   :component-did-mount component-did-mount
                   :component-will-unmount component-will-unmount}))


(defn chart-page []
(fn []
  [:div 
       [gohome]
       [settitle "Charting from Vigiglobe API"]

   [:div {:class "container"}
    [:div {:class "row"}
     [:div {:class "col-sm-6"}
      [chart-component render1 graph-component-mount1  stop-timer1]
      ]

     [:div {:class "col-sm-6"}
      [chart-component render2 graph-component-mount2 stop-timer2]
      ]
     ]]
       ]
      
      )
  )

