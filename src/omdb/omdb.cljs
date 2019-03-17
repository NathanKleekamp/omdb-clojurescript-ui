(ns omdb.omdb
  (:require [ajax.core :as ajax]
            [clojure.string :as string]))


(defn get-api-url [term]
  (let [api {:api-key "BanMePlz"
             :hostname "http://www.omdbapi.com"}]
    (str (:hostname api) "?apikey=" (:api-key api) "&t=" term)))


(defn build-entry [result]
  (let [fragment (. js/document createDocumentFragment)
        entry-title (. js/document createElement "h3")
        entry-director (. js/document createElement "p")
        entry-writer (. js/document createElement "p")
        entry-plot (. js/document createElement "p")
        entry-release (. js/document createElement "p")
        {title :Title
         director :Director
         writer :Writer
         plot :Plot
         release-date :Released} result]
    (set! (.-textContent entry-title) title)
    (set! (.-textContent entry-director) (str "Directed by: " director))
    (set! (.-textContent entry-writer) (str "Written by: " writer))
    (set! (.-textContent entry-plot) plot)
    (set! (.-textContent entry-release) (str "Released: " release-date))
    (reduce (fn [accum current]
              (let [root-node (.getRootNode accum)]
                (.appendChild root-node current))
              accum)
      fragment
      [entry-title entry-plot entry-director entry-writer entry-release])))


(defn handle-result [result]
  (let [resultsDiv (. js/document getElementById "search-results")
        entry (build-entry result)]
    (.appendChild resultsDiv entry)))


(defn handle-submit [event]
  (let [form (.-target event)
        title (.-value (.querySelector form "#search-title"))]
    (.preventDefault event)
    (if-not (string/blank? title)
      (ajax/GET (get-api-url title) {:handler handle-result
                                     :response-format (ajax/json-response-format {:keywords? true})}))))


(defn main []
  (let [searchForm (. js/document getElementById "search-form")]
    (.addEventListener searchForm "submit" handle-submit)))

(main)
