(ns lojbar.core
  (:gen-class)
  (:require
   [lojbar.custom :as custom]
   [lojbar.handler :as handler]
   [lojbar.format :as format]
   [lojbar.subscribe :as subscribe]))

(def bar (format/make-region-modules-map (:format format/conf)))
(def atoms (map :atom (flatten (map val bar)))) ;;  ew
(def outputf
  #(println
    (if (= (:output format/conf) :lemonbar)
      (format/spew-lemonbar bar)
      "Not implemented yet :(")))

(defn -main [& args]
  (doseq [a atoms]
    (subscribe/run-on-atom-change a outputf)))
