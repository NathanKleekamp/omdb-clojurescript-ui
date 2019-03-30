(ns omdb.omdb
  (:require [goog.dom :as dom]
            [goog.dom.forms :as forms]
            [goog.events :as events]
            [ajax.core :as ajax]
            [clojure.string :as string]))


(defn get-api-url [term]
  (let [api {:api-key "a7473ecd"
             :hostname "http://www.omdbapi.com"}]
    (str (:hostname api) "?apikey=" (:api-key api) "&t=" term)))


(defn build-entry [result]
  (let [fragment (dom/createElement "div")
        entry-title (dom/createElement "h3")
        entry-director (dom/createElement "p")
        entry-writer (dom/createElement "p")
        entry-plot (dom/createElement "p")
        entry-release (dom/createElement "p")
        {title :Title
         director :Director
         writer :Writer
         plot :Plot
         release-date :Released} result]
    (dom/setTextContent entry-title title)
    (dom/setTextContent entry-director (str "Directed by: " director))
    (dom/setTextContent entry-writer (str "Written by: " writer))
    (dom/setTextContent entry-plot plot)
    (dom/setTextContent entry-release) (str "Released: " release-date)
    (reduce (fn [accum current]
              (let [root-node (.getRootNode accum)]
                (dom/appendChild root-node current))
              accum)
            fragment
            [entry-title entry-plot entry-director entry-writer entry-release])))


(defn handle-result [result]
  (let [resultsDiv (dom/getElement "search-results")
        entry (build-entry result)]
    (dom/appendChild resultsDiv entry)))


(defn handle-submit [event]
  (let [title (dom/getElement "search-title")]
    (.preventDefault event)
    (if-not (string/blank? title)
      (ajax/GET (get-api-url (forms/getValue title)) {:handler handle-result
                                     :response-format (ajax/json-response-format {:keywords? true})}))))


(defn main []
  (let [searchForm (dom/getElement "search-form")]
    (events/listen searchForm events/EventType.SUBMIT handle-submit)))

(main)
