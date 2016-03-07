(ns testvg.core
    (:require [reagent.core :as r :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [testvg.common :refer [gohome settitle] ]
              [testvg.bonus :as bonus]
              [testvg.chart :as chart]
              [goog.history.EventType :as EventType])
    (:import goog.History))

;; -------------------------
;; Views

(defn home-page []

 
  [:div {:class "container"}
   [:div {:class "jumbotron"}
    [:div {:class "row"} [:h1  "Welcome to testvg"]
     [:div {:class "row"} [:a {:href "#/about"} "go to about page"]]
     [:div {:class "row"}[:a {:href "#/test1"} "go to page: Test 1"]]
     [:div {:class "row"}[:a {:href "#/test2"} "go to page: Test 2"]]
     [:div {:class "row"} [:a {:href "#/test3"} "go to page: Test 3"]]
     [:div {:class "row"}[:a {:href "#/bonus"} "go to page: Bonus"]]
     [:div {:class "row"}[:a {:href "#/chart"} "go to page: Chart"]]
     ]]])







(defn about-page []
  [:div {:class "container"}
   [gohome]
   [settitle "About..."]   
   
   [:div {:class "row"}
    [:div {:class "col-sm-12"}
     "I had a lot of fun with this project. I did not know much about clojurescript and this is my very first usage of reagent"]

    

    ]

   [:div {:class "row"}
    

    [:div {:class "col-sm-8"}
     "The first 3 tests were easier than the rest. The bonus page challenging part was about trying to find the correct data structure for handling the counters"]

    ]

   [:div {:class "row"}
    

    [:div {:class "col-sm-8"}
     "The most challenging part was the charting page (not so much the vigiglobe API usage which is easy to grab. Overall I had a great time, and no matter of the future, I am thankful I had the opportunity to code this.

"]

    ]

   [:div {:class "row"}
    

    [:div {:class "col-sm-4"}
     "Eric"]

    ]
   ])





(defn current-page []
  [:div [(session/get :current-page)]])


(defn test1-page []
  (let [seconds-elapsed (atom 0)]


    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)

      [:div {:class "container"}
       [gohome]
       [settitle "Test 1..."]   
       
       [:div {:class "row"} [:span {:class "label label-success"} "Seconds Elapsed : " [:span {:class "badge"} @seconds-elapsed]] ]
       ]
       )))
        

  
(defn test2-page []
  (let [seconds-elapsed (atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
       [:div {:class "container"}
        [gohome]
        [settitle "Test 2..."]   
       
        [:div {:class "row"} [:span {:class "label label-success"} "Seconds Elapsed : " [:span {:class "badge"}@seconds-elapsed]]]
        [:div {:class "row"} [:span "Other shared value : " [:span {:class "badge"}@seconds-elapsed]]]
        ]
        )))

(defn test3-page []
  (let [seconds-elapsed (atom 0)
        chrono-id (atom 0)
        running? (atom true)]
    (fn []
      (when @running?
        (->>  (js/setTimeout #(swap! seconds-elapsed inc) 1000)
        (reset! chrono-id))
        )
      [:div {:class "container"}
       [gohome]
       [settitle "Test 3"]
       
       [:div {:class "row"}
        [:div {:class "col-sm-8"}
         [:span {:class "label label-success"} "Seconds Elapsed : " [:span {:class "badge"}@seconds-elapsed]]]]
       [:div "Other shared value : " [:span {:class "badge"}@seconds-elapsed]]
       [:button {:class "btn btn-default navbar-btn"
                 :on-click (fn [] (when @running?
                                    (js/clearTimeout @chrono-id))
                             (swap! running? #(not %)))}
        (if @running? "Stop" "Start")]
       ])))


;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

(secretary/defroute "/test1" []
  (session/put! :current-page #'test1-page))

(secretary/defroute "/test2" []
  (session/put! :current-page #'test2-page))

(secretary/defroute "/test3" []
  (session/put! :current-page #'test3-page))

(secretary/defroute "/bonus" []
  (session/put! :current-page #'bonus/bonus-page))

(secretary/defroute "/chart" []
  (session/put! :current-page #'chart/chart-page))



;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

