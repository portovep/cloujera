(ns cloujera.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "Hello world!")


(def app-state
  (atom
    {:videos [{}]}))

(defn handle-input [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))

(defn search [app owner]
  (let [input (om/get-node owner "search-query")
        search-query (-> input .-value)]
    (when search-query
      (println (str "Search query=" search-query)))))


;; search bar component
(defn search-bar-view [app owner]
  (reify

    om/IInitState
    (init-state [_]
      {:text ""})

    om/IRenderState
    (render-state [this state]
      (dom/input #js {:type "text"
                      :ref "search-query"
                      :value (:text state)
                      :onChange #(handle-input % owner state)}
      (dom/button #js {:onClick #(search app owner)}
                      "Search")))))

(om/root
  search-bar-view
  app-state
  {:target (. js/document (getElementById "search-bar"))})
