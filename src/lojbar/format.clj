(ns lojbar.format
  (:gen-class)
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [lojbar.handler :as handler])
  (:use  [lojbar.module :only [make-module]]))

(defn parse-cfg [file]
  (-> file slurp edn/read-string))

(defn button->num [invert-scrolling?]
  {
   :left   1   :middle 2  :right  3
   :up    (if invert-scrolling? 5 4)
   :down  (if invert-scrolling? 4 5)
   :in     6              :out    7})

(defn format-color [fg bg]
  (str "%{F" fg "}"  "%{B" bg "}"))

(def conf (-> "config.edn" parse-cfg)) 
(def default-color
  (format-color (:fg-default conf) (:bg-default conf)))
(def bold-color
  (format-color (:fg-bold conf) (:bg-bold conf))) 
(def buttons-as-nums
  (button->num (:invert-scrolling conf)))

(defn make-buttons-lemonbar [text buttons]
  (reduce
   (fn [acc [k cmd]]
     (str
      "%{A" (k buttons-as-nums) ":" cmd ":}" acc "%{A}"))
   text buttons))

(defn module-make-buttons-lemonbar
  "Turn a given module into a full-fledged button"
  [m]
  ;; todo: add module's color per lemonbar syntax
  ;; todo: generalize
  (let [buttons (:buttons m)
        text @(:atom m)]
    (if buttons
      (make-buttons-lemonbar text buttons)
      text)))
 
(defn generate-region-buttons-lemonbar
  "Given a region and a list of modules, make and format all the buttons for that region"
  [mlist region]
  ;; todo: generalize coloring?
  (str default-color
       "%{" (str (first region)) "}"
       (str/join
        (str default-color " ")
        (map #(if (:buttons %) (module-make-buttons-lemonbar %) @(:atom %)) mlist))))

(defn lookup-region-modules
  "Lookup the actual modules given their names"
  [mlist]
  (map #(-> conf :modules %) mlist))

(defn make-region-modules [region-mapentry]
  (let [region-str (name (key region-mapentry))
        cfgmodulelist (lookup-region-modules (val region-mapentry))]
    (map make-module cfgmodulelist)))

(defn make-region-modules-map [region-hashmap]
  (reduce
   #(assoc %1
           (key %2)
           (make-region-modules %2))
   region-hashmap
   region-hashmap))

(defn spew-lemonbar [region-modules-map]
  (str/join " "
   (map
    #(generate-region-buttons-lemonbar (val %) (name (key %)))
    region-modules-map)))
