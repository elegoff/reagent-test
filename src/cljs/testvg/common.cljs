(ns testvg.common )

(defn gohome []
  (fn []
    [:div {:class "row"} [:a {:href "/"} [:img {:src "images/home.png"}]" Go home"]]
    )
  )

(defn settitle [t]
  (fn []
    [:div {:class "row"} [:div {:class "page-header"} [:h1 t]]]
  ))
