(ns testvg.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [testvg.middleware :refer [wrap-middleware]]
            [environ.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(def loading-page
  (html5
   [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1"}]
    (include-css "css/bootstrap.min.css" (if (env :dev) "css/site.css" "css/site.min.css"))
    (include-js "js/jquery-2.2.1.min.js"
                 "js/highcharts.js"
                 "js/highcharts-more.js"
                 "js/modules/exporting.js")
     ]
    [:body
     mount-target
     
     (include-js "js/app.js")]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/about" [] loading-page)
  (GET "/test1" [] loading-page)
  (GET "/test2" [] loading-page)
  (GET "/test3" [] loading-page)
  (GET "/chart" [] loading-page)
  (GET "/bonus" [] loading-page)
  
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
